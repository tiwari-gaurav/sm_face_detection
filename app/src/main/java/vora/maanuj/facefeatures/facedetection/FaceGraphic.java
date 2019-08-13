// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package vora.maanuj.facefeatures.facedetection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.google.android.gms.vision.CameraSource;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import vora.maanuj.facefeatures.common.GraphicOverlay;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 4.0f;
    private static final float ID_TEXT_SIZE = 30.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private int facing;

    private final Paint facePositionPaint;
    private final Paint idPaint;
    private final Paint boxPaint;

    private volatile FirebaseVisionFace firebaseVisionFace;

    private final Bitmap overlayBitmap;

    private float[] leftx = new float[4];
    private float[] rightx = new float[4];
    private float[] lefty = new float[4];
    private float[] righty = new float[4];

    public FaceGraphic(GraphicOverlay overlay, FirebaseVisionFace face, int facing, Bitmap overlayBitmap) {
        super(overlay);

        firebaseVisionFace = face;
        this.facing = facing;
        this.overlayBitmap = overlayBitmap;
        final int selectedColor = Color.WHITE;

        facePositionPaint = new Paint();
        facePositionPaint.setColor(selectedColor);

        idPaint = new Paint();
        idPaint.setColor(selectedColor);
        idPaint.setTextSize(ID_TEXT_SIZE);

        boxPaint = new Paint();
        boxPaint.setColor(selectedColor);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        FirebaseVisionFace face = firebaseVisionFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        // An offset is used on the Y axis in order to draw the circle, face id and happiness level in the top area
        // of the face's bounding box
        float x = translateX(face.getBoundingBox().centerX());
        float y = translateY(face.getBoundingBox().centerY());
        canvas.drawCircle(x, y - 4 * ID_Y_OFFSET, FACE_POSITION_RADIUS, facePositionPaint);
        canvas.drawText("id: " + face.getTrackingId(), x + ID_X_OFFSET, y - 3 * ID_Y_OFFSET, idPaint);


        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
        float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, boxPaint);

        // draw landmarks
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.MOUTH_BOTTOM);
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_CHEEK);
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_EAR);
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.MOUTH_LEFT);
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.LEFT_EYE);
        drawBitmapOverLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.NOSE_BASE);
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_CHEEK);
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_EAR);
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.RIGHT_EYE);
        drawLandmarkPosition(canvas, face, FirebaseVisionFaceLandmark.MOUTH_RIGHT);
        drawLandmarkLine(canvas, face);
    }

    private void drawLandmarkPosition(Canvas canvas, FirebaseVisionFace face, int landmarkID) {
        FirebaseVisionFaceLandmark landmark = face.getLandmark(landmarkID);
        if (landmark != null) {
            FirebaseVisionPoint point = landmark.getPosition();
            canvas.drawCircle(
                    translateX(point.getX()),
                    translateY(point.getY()),
                    10f, idPaint);


            if(landmarkID == FirebaseVisionFaceLandmark.MOUTH_BOTTOM){
                leftx[0] = point.getX();
                lefty[0] = point.getY();

                rightx[0] = point.getX();
                righty[0] = point.getY();
            }
            else if(landmarkID == FirebaseVisionFaceLandmark.MOUTH_LEFT){
                leftx[1] = point.getX();
                lefty[1] = point.getY();
            }
            else if(landmarkID == FirebaseVisionFaceLandmark.LEFT_CHEEK){
                leftx[2] = point.getX();
                lefty[2] = point.getY();
            }
            else if(landmarkID == FirebaseVisionFaceLandmark.LEFT_EAR){
                leftx[3] = point.getX();
                lefty[3] = point.getY();
            }
            else if(landmarkID == FirebaseVisionFaceLandmark.MOUTH_RIGHT){
                rightx[1] = point.getX();
                righty[1] = point.getY();
            }
            else if(landmarkID == FirebaseVisionFaceLandmark.RIGHT_CHEEK){
                rightx[2] = point.getX();
                righty[2] = point.getY();
            }
            else if(landmarkID == FirebaseVisionFaceLandmark.RIGHT_EAR){
                rightx[3] = point.getX();
                righty[3] = point.getY();
            }
        }
    }

    private void drawLandmarkLine(Canvas canvas, FirebaseVisionFace face){
        for(int x = 0; x < leftx.length; x++){
            if(x == 3){
            }
            else{
                canvas.drawLine(translateX(leftx[x]), translateY(lefty[x]), translateX(leftx[x+1]), translateY(lefty[x+1]), idPaint);
            }
        }

        for(int x = 0; x < rightx.length; x++){
            if(x == 3){
            }
            else{
                canvas.drawLine(translateX(rightx[x]), translateY(righty[x]), translateX(rightx[x+1]), translateY(righty[x+1]), idPaint);
            }
        }
    }

    private void drawBitmapOverLandmarkPosition(Canvas canvas, FirebaseVisionFace face, int landmarkID) {
        FirebaseVisionFaceLandmark landmark = face.getLandmark(landmarkID);
        if (landmark == null) {
            return;
        }

        FirebaseVisionPoint point = landmark.getPosition();

        if (overlayBitmap != null) {
            float imageEdgeSizeBasedOnFaceSize = (face.getBoundingBox().width() / 4.0f);

            int left = (int) (translateX(point.getX()) - imageEdgeSizeBasedOnFaceSize);
            int top = (int) (translateY(point.getY()) - imageEdgeSizeBasedOnFaceSize);
            int right = (int) (translateX(point.getX()) + imageEdgeSizeBasedOnFaceSize);
            int bottom = (int) (translateY(point.getY()) + imageEdgeSizeBasedOnFaceSize);

            canvas.drawBitmap(overlayBitmap,
                    null,
                    new Rect(left, top, right, bottom),
                    null);
        }

    }
}
