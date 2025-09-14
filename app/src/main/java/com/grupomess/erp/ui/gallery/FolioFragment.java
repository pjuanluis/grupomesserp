package com.grupomess.erp.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.grupomess.erp.R;
import com.grupomess.erp.databinding.FragmentFolioBinding;

public class FolioFragment extends Fragment {

    private FragmentFolioBinding binding;
    private ImageButton scanButton;
    private EditText folioEditText;

    // Lanzador para permisos de cámara
    private ActivityResultLauncher<String> requestPermissionLauncher;

    // Lanzador para la cámara
    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el lanzador de permisos
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                });

        // Inicializamos el lanzador de cámara
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        scanTextFromImage(bitmap);
                    }
                });
    }

    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          android.view.ViewGroup container,
                                          Bundle savedInstanceState) {
        new ViewModelProvider(this).get(FolioViewModel.class);

        binding = FragmentFolioBinding.inflate(inflater, container, false);
        scanButton = binding.getRoot().findViewById(R.id.scanButton);
        folioEditText = binding.getRoot().findViewById(R.id.folioEditText);

        scanButton.setOnClickListener(v -> checkCameraPermission());

        return binding.getRoot();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            takePictureLauncher.launch(intent);
        } else {
            Toast.makeText(getContext(), "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void scanTextFromImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> folioEditText.setText(visionText.getText()))
                .addOnFailureListener(e -> {
                    Log.e("FolioFragment", "Error al escanear", e);
                    Toast.makeText(getContext(), "Error al escanear: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
