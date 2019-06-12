/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    // TODO (3): Create threshold constants for a person smiling, and and eye being open by taking pictures of yourself and your friends and noting the logs.
    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    // TODO (1): Create an enum class called Emoji that contains all the possible emoji you can make
    //  (smiling, frowning, left wink, right wink, left wink frowning, right wink frowning, closed eye smiling, close eye frowning).
    // Enum for all possible Emojis
    private enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }

    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    public static void detectFaces(final Context context, Bitmap picture) {

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

                                        if (faces.size() == 0) {
                                            Toast.makeText(context, "No Faces Detected", Toast.LENGTH_SHORT).show();
                                        }
                                        for (FirebaseVisionFace face : faces) {
                                            // TODO (6): Change the call to getClassifications to whichEmoji() to log the appropriate emoji for the facial expression.
                                            whichEmoji(face);
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


    // TODO (2): Change the name of the getClassifications() method to whichEmoji() (also change the log statements)
    private static void whichEmoji(FirebaseVisionFace face) {

        // Log all the probabilities
        Log.d(LOG_TAG, "getClassifications: smilingProb = " + face.getSmilingProbability());
        Log.d(LOG_TAG, "getClassifications: leftEyeOpenProb = "
                + face.getLeftEyeOpenProbability());
        Log.d(LOG_TAG, "getClassifications: rightEyeOpenProb = "
                + face.getRightEyeOpenProbability());

        // TODO (4): Create 3 boolean variables to track the state of the facial expression based on the thresholds you set in the previous step:
        //  smiling, left eye closed, right eye closed.
        boolean smiling = face.getSmilingProbability() > SMILING_PROB_THRESHOLD;
        boolean leftEyeClosed = face.getLeftEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;
        boolean rightEyeClosed = face.getRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;

        // TODO (5): Create an if/else system that selects the appropriate emoji based on the above booleans and log the result.
        // Determine and log the appropriate emoji
        Emoji emoji;
        if (smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK;
            } else if (rightEyeClosed && !leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {
                emoji = Emoji.SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK_FROWN;
            } else if (rightEyeClosed && !leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.FROWN;
            }
        }

        // Log the chosen Emoji
        Log.d(LOG_TAG, "whichEmoji: " + emoji.name());
    }


}
