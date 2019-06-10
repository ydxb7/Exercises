package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

// TODO (1): Create a Java class called Emojifier
// TODO (2): Create a static method in the Emojifier class called detectFaces() which detects and logs the number of faces in a given bitmap.
public class Emojifier {
    public static final String LOG_TAG = Emojifier.class.getSimpleName();

    public static void detectFaces(final Context context, Bitmap picture){

        // Configure the face detector
        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();

        // Get an instance of FirebaseVisionFaceDetector:
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(highAccuracyOpts);

        // Create a FirebaseVisionImage object from your image.
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(picture);

        // pass the image to the detectInImage method
        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        // ...
                                        Log.d(LOG_TAG, "detectFaces: number of faces = " + faces.size());

                                        if(faces.size() == 0){
                                            Toast.makeText(context, "No Faces Detected", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        Log.d(LOG_TAG, "Failed to detect the faces.");
                                    }
                                });

    }




}
