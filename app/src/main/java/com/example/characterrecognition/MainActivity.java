
package com.example.characterrecognition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.example.characterrecognition.ml.EffnetEmnist;
import com.example.characterrecognition.ml.MobilenetEmnist;
import com.example.characterrecognition.ml.VCnnSvhn;
import com.example.characterrecognition.ml.MobilenetSvhn;
import com.yalantis.ucrop.UCrop;

public class MainActivity extends AppCompatActivity {

    Button camera, gallery;
    ImageView imageView;
    TextView resultEffnet, effnetAccuracy, effnetTime;
    TextView resultMobilenet, mobilenetAccuracy, mobilenetTime;
    TextView resultVcnn, vcnnAccuracy, vcnnTime;
    TextView resultMobileNetSvhn, mobilenetSvhnAccuracy, mobilenetSvhnTime;
    Spinner datasetSpinner;
    int imageSize = 32;
    String selectedDataset = "EMNIST";
    Uri cropUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datasetSpinner = findViewById(R.id.dataset_spinner);
        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);

        resultEffnet = findViewById(R.id.result_effnet);
        resultEffnet = findViewById(R.id.result_effnet);
        effnetAccuracy = findViewById(R.id.effnet_accuracy);
        effnetTime = findViewById(R.id.effnet_time);

        resultMobilenet = findViewById(R.id.result_mobilenet);
        mobilenetAccuracy = findViewById(R.id.mobilenet_accuracy);
        mobilenetTime = findViewById(R.id.mobilenet_time);

        resultVcnn = findViewById(R.id.result_vcnn);
        vcnnAccuracy = findViewById(R.id.vcnn_accuracy);
        vcnnTime = findViewById(R.id.vcnn_time);

        resultMobileNetSvhn = findViewById(R.id.result_mobilenet_svhn);
        mobilenetSvhnAccuracy = findViewById(R.id.mobilenet_svhn_accuracy);
        mobilenetSvhnTime = findViewById(R.id.mobilenet_svhn_time);

        imageView = findViewById(R.id.imageView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.datasets, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        datasetSpinner.setAdapter(adapter);

        datasetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDataset = parent.getItemAtPosition(position).toString();
                updateResultVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });
    }

    private void updateResultVisibility() {
        imageView.setVisibility(View.GONE); // Ascunde imaginea
        imageView.setImageDrawable(null); // Resetează imaginea

        resultEffnet.setText(""); // Resetează textul de clasificare
        effnetAccuracy.setText(""); // Resetează textul de acuratețe
        effnetTime.setText(""); // Resetează textul de timp

        resultMobilenet.setText(""); // Resetează textul de clasificare
        mobilenetAccuracy.setText(""); // Resetează textul de acuratețe
        mobilenetTime.setText(""); // Resetează textul de timp

        resultVcnn.setText(""); // Resetează textul de clasificare
        vcnnAccuracy.setText(""); // Resetează textul de acuratețe
        vcnnTime.setText(""); // Resetează textul de timp

        resultMobileNetSvhn.setText(""); // Resetează textul de clasificare
        mobilenetSvhnAccuracy.setText(""); // Resetează textul de acuratețe
        mobilenetSvhnTime.setText(""); // Resetează textul de timp

        if (selectedDataset.equals("EMNIST")) {
            resultEffnet.setVisibility(View.VISIBLE);
            effnetAccuracy.setVisibility(View.VISIBLE);
            effnetTime.setVisibility(View.VISIBLE);

            resultMobilenet.setVisibility(View.VISIBLE);
            mobilenetAccuracy.setVisibility(View.VISIBLE);
            mobilenetTime.setVisibility(View.VISIBLE);

            resultVcnn.setVisibility(View.GONE);
            vcnnAccuracy.setVisibility(View.GONE);
            vcnnTime.setVisibility(View.GONE);

            resultMobileNetSvhn.setVisibility(View.GONE);
            mobilenetSvhnAccuracy.setVisibility(View.GONE);
            mobilenetSvhnTime.setVisibility(View.GONE);
        } else if (selectedDataset.equals("SVHN")) {
            resultEffnet.setVisibility(View.GONE);
            effnetAccuracy.setVisibility(View.GONE);
            effnetTime.setVisibility(View.GONE);

            resultMobilenet.setVisibility(View.GONE);
            mobilenetAccuracy.setVisibility(View.GONE);
            mobilenetTime.setVisibility(View.GONE);

            resultVcnn.setVisibility(View.VISIBLE);
            vcnnAccuracy.setVisibility(View.VISIBLE);
            vcnnTime.setVisibility(View.VISIBLE);

            resultMobileNetSvhn.setVisibility(View.VISIBLE);
            mobilenetSvhnAccuracy.setVisibility(View.VISIBLE);
            mobilenetSvhnTime.setVisibility(View.VISIBLE);
        }
    }


    public void classifyImage(Bitmap image){
        try {
            if (selectedDataset.equals("EMNIST")) {
                classifyImageEmnist(image);
            } else if (selectedDataset.equals("SVHN")) {
                classifyImageSVHN(image);
            }
        } catch (IOException e) {
            // Gestionează excepția
            e.printStackTrace();
        }
    }


    public void classifyImageEmnist(Bitmap image) throws IOException {
        try {
            // Clasificarea cu EffNet
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            EffnetEmnist effnetmodel = EffnetEmnist.newInstance(getApplicationContext());

            // Creează intrări pentru referință.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            // Iterare peste fiecare pixel și extragerea valorilor R, G, și B. Adăugarea acestor valori individual în buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                    byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                    byteBuffer.putFloat((val & 0xFF) / 255.0f);
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Rulează inferența modelului și obține rezultatul.
            EffnetEmnist.Outputs outputs = effnetmodel.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // Găsește indexul clasei cu cea mai mare încredere.
            int effnetMaxPos = 0;
            float effnetMaxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > effnetMaxConfidence) {
                    effnetMaxConfidence = confidences[i];
                    effnetMaxPos = i;
                }
            }
            String[] classes = {"d", "e", "f", "g", "h", "n", "q", "r", "t", "#", "$", "&", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b"};
            endTime = System.currentTimeMillis();
            resultEffnet.setText("EffNet: " + classes[effnetMaxPos]);
            effnetAccuracy.setText("Încredere: " + String.format("%.2f", (effnetMaxConfidence * 100)) + "%");
            effnetTime.setText("Durată predicție: " + (endTime - startTime) + " ms");



            // Eliberează resursele modelului dacă nu mai sunt utilizate.
            effnetmodel.close();

            // Clasificarea cu MobileNet
            startTime = System.currentTimeMillis();
            MobilenetEmnist mobilenetModel = MobilenetEmnist.newInstance(getApplicationContext());

            // Creează intrări pentru referință.
            TensorBuffer mobilenetInputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);
            ByteBuffer mobilenetByteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            mobilenetByteBuffer.order(ByteOrder.nativeOrder());

            pixel = 0;
            // Iterare peste fiecare pixel și extragerea valorilor R, G, și B. Adăugarea acestor valori individual în buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    mobilenetByteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                    mobilenetByteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                    mobilenetByteBuffer.putFloat((val & 0xFF) / 255.0f);
                }
            }

            mobilenetInputFeature0.loadBuffer(mobilenetByteBuffer);

            // Rulează inferența modelului și obține rezultatul.
            MobilenetEmnist.Outputs mobilenetOutputs = mobilenetModel.process(mobilenetInputFeature0);
            TensorBuffer mobilenetOutputFeature0 = mobilenetOutputs.getOutputFeature0AsTensorBuffer();

            float[] mobilenetConfidences = mobilenetOutputFeature0.getFloatArray();
            // Găsește indexul clasei cu cea mai mare încredere.
            int mobilenetMaxPos = 0;
            float mobilenetMaxConfidence = 0;
            for (int i = 0; i < mobilenetConfidences.length; i++) {
                if (mobilenetConfidences[i] > mobilenetMaxConfidence) {
                    mobilenetMaxConfidence = mobilenetConfidences[i];
                    mobilenetMaxPos = i;
                }
            }
            endTime = System.currentTimeMillis();
            long ti= endTime - startTime;
            resultMobilenet.setText("MobileNet: " + classes[mobilenetMaxPos]);
            mobilenetAccuracy.setText("Încredere: " + String.format("%.2f", (mobilenetMaxConfidence * 100)) + "%");
            mobilenetTime.setText("Durată predicție: " + (endTime - startTime) + " ms");
            // Eliberează resursele modelului dacă nu mai sunt utilizate.
            mobilenetModel.close();

        } catch (IOException e) {
            // TODO Handle the exception
            e.printStackTrace();
        }
    }


    public void classifyImageSVHN(Bitmap image) throws IOException {
        long startTime, endTime;
        startTime = System.currentTimeMillis();

        VCnnSvhn model = VCnnSvhn.newInstance(getApplicationContext());

        // Pregătește datele de intrare
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[imageSize * imageSize];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        int pixel = 0;
        for(int i = 0; i < imageSize; i ++){
            for(int j = 0; j < imageSize; j++){
                int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                byteBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }

        inputFeature0.loadBuffer(byteBuffer);

        // Rulează inferența modelului și obține rezultatul.
        VCnnSvhn.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

        float[] vcnnConfidences = outputFeature0.getFloatArray();
        int vcnnMaxPos = 0;
        float vcnnMaxConfidence = 0;
        for (int i = 0; i < vcnnConfidences.length; i++) {
            if (vcnnConfidences[i] > vcnnMaxConfidence) {
                vcnnMaxConfidence = vcnnConfidences[i];
                vcnnMaxPos = i;
            }
        }
        endTime = System.currentTimeMillis();
        String[] classes = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        resultVcnn.setText("V-CNN: " + classes[vcnnMaxPos]);
        vcnnAccuracy.setText("Încredere: " + String.format("%.2f", (vcnnMaxConfidence * 100)) + "%");
        vcnnTime.setText("Durată predicție: " + (endTime - startTime) + " ms");

        model.close();

        // Clasificarea cu MobileNet
        startTime = System.currentTimeMillis();
        MobilenetSvhn mobilenetModel = MobilenetSvhn.newInstance(getApplicationContext());

        // Creează intrări pentru referință.
        TensorBuffer mobilenetInputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);
        ByteBuffer mobilenetByteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
        mobilenetByteBuffer.order(ByteOrder.nativeOrder());

        pixel = 0;
        // Iterare peste fiecare pixel și extragerea valorilor R, G, și B. Adăugarea acestor valori individual în buffer.
        for(int i = 0; i < imageSize; i ++){
            for(int j = 0; j < imageSize; j++){
                int val = intValues[pixel++]; // RGB
                mobilenetByteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                mobilenetByteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                mobilenetByteBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }

        mobilenetInputFeature0.loadBuffer(mobilenetByteBuffer);

        // Rulează inferența modelului și obține rezultatul.
        MobilenetSvhn.Outputs mobilenetOutputs = mobilenetModel.process(mobilenetInputFeature0);
        TensorBuffer mobilenetOutputFeature0 = mobilenetOutputs.getOutputFeature0AsTensorBuffer();

        float[] mobilenetConfidences = mobilenetOutputFeature0.getFloatArray();
        // Găsește indexul clasei cu cea mai mare încredere.
        int mobilenetMaxPos = 0;
        float mobilenetMaxConfidence = 0;
        for (int i = 0; i < mobilenetConfidences.length; i++) {
            if (mobilenetConfidences[i] > mobilenetMaxConfidence) {
                mobilenetMaxConfidence = mobilenetConfidences[i];
                mobilenetMaxPos = i;
            }
        }
        endTime = System.currentTimeMillis();
        long ti= endTime - startTime;
        resultMobileNetSvhn.setText("MobileNet: " + classes[mobilenetMaxPos]);
        mobilenetSvhnAccuracy.setText("Încredere: " + String.format("%.2f", (mobilenetMaxConfidence * 100)) + "%");
        mobilenetSvhnTime.setText("Durată predicție: " + (endTime - startTime) + " ms");
        // Eliberează resursele modelului dacă nu mai sunt utilizate.
        mobilenetModel.close();
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);
                imageView.setVisibility(View.VISIBLE);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);
                imageView.setVisibility(View.VISIBLE);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
*/
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
        // Dacă rezultatul provine de la cameră se preia imaginea, se convertește într-un URI și se taie
        if (requestCode == 3) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            Uri imageUri = getImageUri(image);
            startCrop(imageUri);
            // Dacă rezultatul provine din galerie se preia imaginea, se convertește într-un URI și se taie
        } else if (requestCode == 1) {
            Uri selectedImage = data.getData();
            startCrop(selectedImage);
            // Dacă rezultatul provine de la activitatea de crop se convertește în URI
            // Se redimensionează și se clasifică
        } else if (requestCode == UCrop.REQUEST_CROP) {
            Uri croppedUri = UCrop.getOutput(data);
            try {
                Bitmap croppedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedUri);
                imageView.setImageBitmap(croppedImage);
                imageView.setVisibility(View.VISIBLE);
                Bitmap scaledImage = Bitmap.createScaledBitmap(croppedImage, imageSize, imageSize, false);
                classifyImage(scaledImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

    private void startCrop(Uri uri) {
    // Fișierul destinație pentru imagine. URI pentru fișier în cache
        String destinationFileName = "sampleCropImage.jpg";
        cropUri = Uri.fromFile(new File(getCacheDir(), destinationFileName));
        // Taierea imaginii cu aspect ratio 1:1 și dimensiunea maximă de 32x32 pixeli
        UCrop.of(uri, cropUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(32, 32)
                .start(this);
    }

    private Uri getImageUri(Bitmap bitmap) {
        // Crearea unui director "camera" în directorul cache dacă nu există
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        // Crearea unui fișier pentru imaginea bitmap
        File imageFile = new File(path, System.currentTimeMillis() + ".jpg");
        try {
            imageFile.createNewFile();
            // Convertirea imaginii bitmap într-un array de byte
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapData = bos.toByteArray();
            // Scrierea datele în fișier
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(imageFile);
    }
}