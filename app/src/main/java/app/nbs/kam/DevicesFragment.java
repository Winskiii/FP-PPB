package app.nbs.kam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DevicesFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "DevicesFragment";

    // --- Variabel UI dan State ---
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng defaultLocation = new LatLng(-6.2088, 106.8456);
    private EditText editTextSearch;
    private ImageButton buttonSearch;
    private ImageView detailsBackButton;
    private TextView textRoadNameValue, textDamageValue, textConfidenceValue, textDamageScaleValue;
    private LinearLayout detailsPanel;
    private SeekBar seekbarConfidence;
    private TextView textConfidenceSliderValue;
    private ImageView roboflowImageViewNewUi, damageImagePlaceholder;
    private CardView chatbotCard;
    private ProgressBar chatbotProgressBar;
    private TextView textChatbotResponse;
    private Bitmap currentBitmap;

    private int currentConfidenceThreshold = 50;
    private JSONArray allPredictions = new JSONArray();

    // --- Konstanta API ---
    private static final String ROBOFLOW_API_URL = "https://detect.roboflow.com/road-damage-fhdff/1?api_key=";
    private static final String GEMINI_API_KEY = "A";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + GEMINI_API_KEY;

    private ActivityResultLauncher<String[]> requestLocationPermissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupActivityResultLaunchers();
        setupListeners();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (getArguments() != null && getArguments().containsKey("captured_image_uri")) {
            String imageUriString = getArguments().getString("captured_image_uri");
            if (imageUriString != null) {
                // Buat executor untuk menjalankan tugas di background
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        Uri imageUri = Uri.parse(imageUriString);
                        Bitmap loadedBitmap;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            loadedBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().getContentResolver(), imageUri));
                        } else {
                            loadedBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                        }
                        currentBitmap = loadedBitmap.copy(Bitmap.Config.ARGB_8888, true);

                        // Setelah selesai, kembali ke UI Thread untuk update tampilan
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                roboflowImageViewNewUi.setImageBitmap(currentBitmap);
                                roboflowImageViewNewUi.setVisibility(View.VISIBLE);
                                damageImagePlaceholder.setVisibility(View.GONE);
                                sendToRoboflow(currentBitmap);
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Gagal memuat gambar", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        checkLocationPermissionAndEnableMyLocation();
    }

    private void initializeViews(View view) {
        editTextSearch = view.findViewById(R.id.edit_text_search);
        buttonSearch = view.findViewById(R.id.button_search);
        textRoadNameValue = view.findViewById(R.id.text_road_name_value);
        textDamageValue = view.findViewById(R.id.text_damage_value);
        textDamageScaleValue = view.findViewById(R.id.text_damage_scale_value);
        textConfidenceValue = view.findViewById(R.id.text_confidence_value);
        detailsPanel = view.findViewById(R.id.details_panel);
        seekbarConfidence = view.findViewById(R.id.seekbar_confidence);
        textConfidenceSliderValue = view.findViewById(R.id.text_confidence_slider_value);
        roboflowImageViewNewUi = view.findViewById(R.id.roboflow_image_view_new_ui);
        damageImagePlaceholder = view.findViewById(R.id.damage_image_placeholder);
        chatbotCard = view.findViewById(R.id.chatbot_card);
        chatbotProgressBar = view.findViewById(R.id.chatbot_progress_bar);
        textChatbotResponse = view.findViewById(R.id.text_chatbot_response);
        detailsBackButton = view.findViewById(R.id.details_back_button);

        seekbarConfidence.setProgress(currentConfidenceThreshold);
        textConfidenceSliderValue.setText(String.format(Locale.getDefault(), "%d%%", currentConfidenceThreshold));
    }

    private void setupActivityResultLaunchers() {
        requestLocationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    if (!isAdded()) return;
                    Boolean fineLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (Boolean.TRUE.equals(fineLocationGranted) || Boolean.TRUE.equals(coarseLocationGranted)) {
                        enableMyLocation();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.location_permission_needed), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void setupListeners() {
        buttonSearch.setOnClickListener(v -> performSearch());
        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        seekbarConfidence.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentConfidenceThreshold = progress;
                textConfidenceSliderValue.setText(String.format(Locale.getDefault(), "%d%%", currentConfidenceThreshold));
                if (fromUser && allPredictions.length() > 0) {
                    filterAndDisplayPredictions();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        detailsBackButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Back button clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int targetWidth) {
        double aspectRatio = (double) bitmap.getHeight() / (double) bitmap.getWidth();
        int targetHeight = (int) (targetWidth * aspectRatio);
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
    }

    private void sendToRoboflow(Bitmap bitmap) {
        if (mMap != null) {
            reverseGeocode(mMap.getCameraPosition().target);
        }
        if (!isAdded()) return;

        Bitmap resizedBitmap = resizeBitmap(bitmap, 640);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        Toast.makeText(requireContext(), getString(R.string.text_processing_image), Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ROBOFLOW_API_URL,
                response -> {
                    if (!isAdded()) return;
                    Log.d(TAG, "Roboflow Response: " + response);
                    try {
                        allPredictions = new JSONObject(response).optJSONArray("predictions");
                        if (allPredictions == null) allPredictions = new JSONArray();
                        filterAndDisplayPredictions();
                    } catch (JSONException e) {
                        Log.e(TAG, "Roboflow JSON Parsing error", e);
                        Toast.makeText(requireContext(), getString(R.string.text_failed_parse_detection_result), Toast.LENGTH_SHORT).show();
                        clearDamageDetails();
                    }
                },
                error -> {
                    if (!isAdded()) return;
                    Log.e(TAG, "VolleyError", error);
                    Toast.makeText(requireContext(), getString(R.string.text_failed_send_image_to_roboflow), Toast.LENGTH_SHORT).show();
                    clearDamageDetails();
                }
        ) {
            @Override public String getBodyContentType() { return "application/x-www-form-urlencoded"; }
            @Override public byte[] getBody() { return encodedImage.getBytes(); }
        };
        queue.add(stringRequest);
    }

    private void filterAndDisplayPredictions() {
        if (!isAdded()) return;

        if (allPredictions == null || allPredictions.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.text_no_damage_detected), Toast.LENGTH_SHORT).show();
            chatbotCard.setVisibility(View.GONE);
            return;
        }

        JSONObject bestPrediction = null;
        double maxConfidence = -1.0;
        try {
            for (int i = 0; i < allPredictions.length(); i++) {
                JSONObject prediction = allPredictions.getJSONObject(i);
                double confidence = prediction.optDouble("confidence") * 100;
                if (confidence >= currentConfidenceThreshold && confidence > maxConfidence) {
                    maxConfidence = confidence;
                    bestPrediction = prediction;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error filtering predictions", e);
            clearDamageDetails();
            return;
        }

        if (bestPrediction != null) {
            String damageClass = bestPrediction.optString("class", "N/A");
            String confidenceText = String.format(Locale.getDefault(), "%.0f%%", maxConfidence);
            String scale = getDamageScale(damageClass);
            String location = textRoadNameValue.getText().toString();

            textDamageValue.setText(damageClass);
            textDamageScaleValue.setText(scale);
            textConfidenceValue.setText(confidenceText);

            saveHistory(currentBitmap, location, damageClass, scale, confidenceText);

            generateAndSendPromptToAI(damageClass, scale, confidenceText, location);
        } else {
            Toast.makeText(requireContext(), "Tidak ada kerusakan di atas threshold " + currentConfidenceThreshold + "%", Toast.LENGTH_SHORT).show();
            chatbotCard.setVisibility(View.GONE);
            clearDamageDetails();
        }
    }

    private String getDamageScale(String damageClass) {
        switch (damageClass.toLowerCase()) {
            case "pothole": return "Major Damage";
            case "patch": return "Moderate Damage";
            case "fatigue crack": return "Minimum Damage";
            default: return "Unknown";
        }
    }

    private void generateAndSendPromptToAI(String damageClass, String scale, String confidence, String location) {
        if (!isAdded()) return;
        if (location.equals(getString(R.string.placeholder_empty)) || location.isEmpty()) {
            location = "lokasi saat ini (detail tidak tersedia)";
        }
        String prompt = String.format(Locale.getDefault(), getString(R.string.gemini_prompt_template),
                damageClass, scale, confidence, location);
        Log.d(TAG, "Generated AI Prompt: " + prompt);
        getGeminiChatResponse(prompt);
    }

    private void getGeminiChatResponse(String prompt) {
        if (!isAdded()) return;

        chatbotCard.setVisibility(View.VISIBLE);
        chatbotProgressBar.setVisibility(View.VISIBLE);
        textChatbotResponse.setText(getString(R.string.chatbot_thinking));

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JSONObject requestBody = new JSONObject();
        try {
            JSONArray contentsArray = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray partsArray = new JSONArray();
            JSONObject part = new JSONObject();
            part.put("text", prompt);
            partsArray.put(part);
            content.put("parts", partsArray);
            contentsArray.put(content);
            requestBody.put("contents", contentsArray);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create Gemini request body", e);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, GEMINI_API_URL, requestBody,
                response -> {
                    if (!isAdded()) return;
                    chatbotProgressBar.setVisibility(View.GONE);
                    try {
                        String botResponse = response.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");
                        textChatbotResponse.setText(botResponse.trim());
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse Gemini response", e);
                        textChatbotResponse.setText(getString(R.string.chatbot_error));
                    }
                },
                error -> {
                    if (!isAdded()) return;
                    chatbotProgressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Gemini API Error", error);
                    if (error.networkResponse != null) {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e(TAG, "Gemini Error Body: " + responseBody);
                        try {
                            JSONObject errorJson = new JSONObject(responseBody);
                            String errorMessage = errorJson.getJSONObject("error").getString("message");
                            Toast.makeText(requireContext(), "Gemini Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            textChatbotResponse.setText(getString(R.string.chatbot_error));
                        }
                    } else {
                        textChatbotResponse.setText(getString(R.string.chatbot_error));
                    }
                }
        );
        queue.add(jsonObjectRequest);
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        if (!isAdded()) return null;
        ContextWrapper cw = new ContextWrapper(requireContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mypath = new File(directory, "damage_" + timeStamp + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    private void saveHistory(Bitmap image, String roadName, String damageType, String scale, String confidence) {
        if (!isAdded() || image == null) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String imagePath = saveImageToInternalStorage(image);
            if (imagePath == null) {
                Log.e(TAG, "Failed to save image to internal storage.");
                return;
            }

            HistoryItem historyItem = new HistoryItem(roadName, damageType, scale, confidence, imagePath);
            HistoryDatabase.getDatabase(requireContext()).historyDao().insert(historyItem);

            if (isAdded()){
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Saved to history", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void performSearch() {
        String searchString = editTextSearch.getText().toString().trim();
        if (!searchString.isEmpty()) {
            hideKeyboard();
            geocodeAddress(searchString);
        }
    }

    private void hideKeyboard() {
        if (!isAdded()) return;
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view == null) {
            view = new View(requireContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void geocodeAddress(String addressString) {
        if (mMap == null || !isAdded()) return;
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        new Thread(() -> {
            try {
                final List<Address> addressList = geocoder.getFromLocationName(addressString, 1);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (addressList != null && !addressList.isEmpty()) {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(latLng).title(addressString));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.toast_address_not_found), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Geocoding failed", e);
            }
        }).start();
    }

    private void checkLocationPermissionAndEnableMyLocation() {
        if (!isAdded()) return;
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            requestLocationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (mMap != null && isAdded()) {
            mMap.setMyLocationEnabled(true);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null && mMap != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                    reverseGeocode(currentLatLng);
                }
            });
        }
    }

    private void reverseGeocode(LatLng latLng) {
        if (!isAdded()) return;
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (addresses != null && !addresses.isEmpty()) {
                            textRoadNameValue.setText(addresses.get(0).getAddressLine(0));
                        } else {
                            textRoadNameValue.setText("Jalan tidak diketahui");
                        }
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Reverse geocoding failed", e);
            }
        }).start();
    }

    private void clearDamageDetails() {
        if (!isAdded()) return;
        textRoadNameValue.setText(getString(R.string.placeholder_empty));
        textDamageValue.setText(getString(R.string.placeholder_empty));
        textConfidenceValue.setText(getString(R.string.placeholder_empty));
        textDamageScaleValue.setText(getString(R.string.placeholder_empty));
        chatbotCard.setVisibility(View.GONE);
    }
}