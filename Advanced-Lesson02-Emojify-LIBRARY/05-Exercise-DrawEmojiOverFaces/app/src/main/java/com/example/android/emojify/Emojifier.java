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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
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

    private static final float EMOJI_SCALE_FACTOR = .9f;
    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    public static Task<List<FirebaseVisionFace>> mResult;

    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    public static void detectFacesAndOverlayEmoji(final Context context, final Bitmap picture) {
        // TODO (3): Change the name of the detectFaces() method to detectFacesAndOverlayEmoji() and the return type from void to Bitmap

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
        mResult = detector.detectInImage(image);



        // TODO (9): Return the resultBitmap
//        return resultBitmap;
    }



    /**
     * Determines the closest emoji to the expression on the face, based on the
     * odds that the person is smiling and has each eye open.
     *
     * @param face The face for which you pick an emoji.
     */
    static Emoji whichEmoji(FirebaseVisionFace face) {

        // TODO (1): Change the return type of the whichEmoji() method from void to Emoji.

        // Log all the probabilities
        Log.d(LOG_TAG, "getClassifications: smilingProb = " + face.getSmilingProbability());
        Log.d(LOG_TAG, "getClassifications: leftEyeOpenProb = "
                + face.getLeftEyeOpenProbability());
        Log.d(LOG_TAG, "getClassifications: rightEyeOpenProb = "
                + face.getRightEyeOpenProbability());

        boolean smiling = face.getSmilingProbability() > SMILING_PROB_THRESHOLD;
        boolean leftEyeClosed = face.getLeftEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;
        boolean rightEyeClosed = face.getRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;

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

        // TODO (2): Have the method return the selected Emoji type.
        return emoji;
    }


    // TODO (6) Create a method called addBitmapToFace() which takes the background bitmap, the Emoji bitmap, and a Face object as arguments and returns the combined bitmap with the Emoji over the face.
    public static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, FirebaseVisionFace face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getBoundingBox().width() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX = face.getBoundingBox().centerX() - face.getBoundingBox().width() / 2;
        float emojiPositionY = face.getBoundingBox().centerY() - face.getBoundingBox().height() / 2 + emojiBitmap.getHeight() / 4;
//        float emojiPositionX =
//                (face.getBoundingBox().centerX() + face.getBoundingBox().width() / 2) - emojiBitmap.getWidth() / 2;
//        float emojiPositionY =
//                (face.getBoundingBox().centerY() + face.getBoundingBox().height() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }

    // Enum for all possible Emojis
    public enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }

}
