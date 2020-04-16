package vora.maanuj.facefeatures.facedetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.gms.vision.face.Landmark;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import vora.maanuj.facefeatures.R;
import vora.maanuj.facefeatures.common.CameraSource;
import vora.maanuj.facefeatures.common.GraphicOverlay;

/**
 * Graphic instance for rendering face contours graphic overlay view.
 */
public class FaceContourGraphic extends GraphicOverlay.Graphic {

    private static final float FACE_POSITION_RADIUS = 4.0f;
    private static final float ID_TEXT_SIZE = 30.0f;
    private static final float ID_Y_OFFSET = 80.0f;
    private static final float ID_X_OFFSET = -70.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private final Paint facePositionPaint;
    private final Paint idPaint;
    private final Paint boxPaint;
    private Drawable mHatGraphic;
    private Context context;
    private CameraSource cameraSource = null;

    private volatile FirebaseVisionFace firebaseVisionFace;
    FirebaseVisionFaceContour contour;
    private float[] leftx = new float[4];
    private float[] rightx = new float[4];
    private float[] lefty = new float[4];
    private float[] righty = new float[4];

    public FaceContourGraphic(GraphicOverlay overlay, FirebaseVisionFace face, Context mContext, CameraSource cameraSource) {
        super(overlay);

        this.firebaseVisionFace = face;
        final int selectedColor = Color.BLUE;

        facePositionPaint = new Paint();
        facePositionPaint.setColor(selectedColor);

        idPaint = new Paint();
        idPaint.setColor(selectedColor);
        idPaint.setTextSize(ID_TEXT_SIZE);
        this.context = mContext;
        this.cameraSource = cameraSource;
        boxPaint = new Paint();
        boxPaint.setColor(selectedColor);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void draw(Canvas canvas) {
        FirebaseVisionFace face = firebaseVisionFace;
        if (face == null) {
            return;
        }
        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getBoundingBox().centerX());
        float y = translateY(face.getBoundingBox().centerY());
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint);
        canvas.drawText("id: " + face.getTrackingId(), x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint);

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
        float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, boxPaint);

//        canvas.drawText("Width = " + face.getBoundingBox().width(), 0, 50, idPaint);
//        canvas.drawText("Height = " + face.getBoundingBox().height(), 0, 100, idPaint);


        contour = face.getContour(FirebaseVisionFaceContour.ALL_POINTS);
        Log.e("Euler Y", String.valueOf(face.getHeadEulerAngleY()));

        for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : contour.getPoints()) {
            float px = translateX(point.getX());
            float py = translateY(point.getY());
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);

            Log.e("points:", point.getX().toString());
        }

        for (int z = 0; z < contour.getPoints().size(); z++) {
            if (z < 36) {
                if (z == 35) {
                    canvas.drawLine(translateX(contour.getPoints().get(z).getX()), translateY(contour.getPoints().get(z).getY()), translateX(contour.getPoints().get(0).getX()), translateY(contour.getPoints().get(0).getY()), idPaint);
                } else {
                    canvas.drawLine(translateX(contour.getPoints().get(z).getX()), translateY(contour.getPoints().get(z).getY()), translateX(contour.getPoints().get(z + 1).getX()), translateY(contour.getPoints().get(z + 1).getY()), idPaint);
                }
            }
        }
        // if(contour.getPoints().size()>0) {
        canvas.drawLine(translateX(contour.getPoints().get(33).getX()), translateY(contour.getPoints().get(33).getY()), translateX(contour.getPoints().get(3).getX()), translateY(contour.getPoints().get(3).getY()), idPaint);
        canvas.drawLine(translateX(contour.getPoints().get(12).getX()), translateY(contour.getPoints().get(12).getY()), translateX(contour.getPoints().get(24).getX()), translateY(contour.getPoints().get(24).getY()), idPaint);
        canvas.drawLine(translateX(contour.getPoints().get(10).getX()), translateY(contour.getPoints().get(10).getY()), translateX(contour.getPoints().get(26).getX()), translateY(contour.getPoints().get(26).getY()), idPaint);
        canvas.drawLine(translateX(contour.getPoints().get(18).getX()), translateY(contour.getPoints().get(18).getY()), translateX(contour.getPoints().get(0).getX()), translateY(contour.getPoints().get(0).getY()), idPaint);
        canvas.drawLine(translateX(contour.getPoints().get(15).getX()), translateY(contour.getPoints().get(15).getY()), translateX(contour.getPoints().get(21).getX()), translateY(contour.getPoints().get(21).getY()), idPaint);
        //TODO below line coordinates are for right eyebrow
        //canvas.drawLine(translateX(contour.getPoints().get(36).getX()), translateY(contour.getPoints().get(36).getY()), translateX(contour.getPoints().get(40).getX()), translateY(contour.getPoints().get(40).getY()), idPaint);
        canvas.drawLine(translateX(contour.getPoints().get(46).getX()), translateY(contour.getPoints().get(46).getY()), translateX(contour.getPoints().get(50).getX()), translateY(contour.getPoints().get(50).getY()), idPaint);
        canvas.drawLine(translateX(contour.getPoints().get(38).getX()), translateY(contour.getPoints().get(38).getY()), translateX(contour.getPoints().get(43).getX()), translateY(contour.getPoints().get(43).getY()), idPaint);

        // draw line for  cheek

        canvas.drawLine(translateX(contour.getPoints().get(56).getX()), translateY(contour.getPoints().get(56).getY()), translateX(contour.getPoints().get(64).getX()), translateY(contour.getPoints().get(64).getY()), idPaint);

        canvas.drawLine(translateX(contour.getPoints().get(72).getX()), translateY(contour.getPoints().get(72).getY()), translateX(contour.getPoints().get(80).getX()), translateY(contour.getPoints().get(80).getY()), idPaint);

        // line from nose to end of eye

        canvas.drawLine(translateX(contour.getPoints().get(56).getX()), translateY(contour.getPoints().get(56).getY()), translateX(contour.getPoints().get(128).getX()), translateY(contour.getPoints().get(128).getY()), idPaint);
        canvas.drawLine(translateX(contour.getPoints().get(80).getX()), translateY(contour.getPoints().get(80).getY()), translateX(contour.getPoints().get(130).getX()), translateY(contour.getPoints().get(130).getY()), idPaint);
        // draw line from face to nose base left and right
        canvas.drawLine(translateX(contour.getPoints().get(130).getX()), translateY(contour.getPoints().get(130).getY()), translateX(contour.getPoints().get(7).getX()), translateY(contour.getPoints().get(7).getY()), idPaint);
        canvas.drawLine(translateX(contour.getPoints().get(128).getX()), translateY(contour.getPoints().get(128).getY()), translateX(contour.getPoints().get(29).getX()), translateY(contour.getPoints().get(29).getY()), idPaint);

        //lips edge
        canvas.drawLine(translateX(contour.getPoints().get(88).getX()), translateY(contour.getPoints().get(88).getY()), translateX(contour.getPoints().get(98).getX()), translateY(contour.getPoints().get(98).getY()), idPaint);

        //eye iris coordinates
        canvas.drawLine(translateX(contour.getPoints().get(60).getX()), translateY(contour.getPoints().get(60).getY()), translateX(contour.getPoints().get(76).getX()), translateY(contour.getPoints().get(76).getY()), idPaint);

// eyebrow coordinates


        // Text Displayed
        float jaw_width = ((contour.getPoints().get(12).getX() / 2.0f) - (contour.getPoints().get(24).getX() / 2.0f));
        float cheek_width = ((contour.getPoints().get(10).getX() / 2.0f) - (contour.getPoints().get(26).getX() / 2.0f));
        float forehead_width = ((contour.getPoints().get(3).getX() / 2.0f) - (contour.getPoints().get(33).getX() / 2.0f));
        float face_height = ((contour.getPoints().get(18).getY() / 2.0f) - (contour.getPoints().get(0).getY() / 2.0f));
        float chin_width = ((contour.getPoints().get(15).getX() / 2.0f) - (contour.getPoints().get(21).getX() / 2.0f));
        float left_eyebrow_width = ((contour.getPoints().get(36).getX() / 2.0f) - (contour.getPoints().get(40).getX() / 2.0f));
        float right_nose_to_cheek_width = ((contour.getPoints().get(128).getX() / 2.0f) - (contour.getPoints().get(26).getX() / 2.0f));


        canvas.drawText("Jaw Width = " + jaw_width, 0, 500, idPaint);
        canvas.drawText("right_cheek Width = " + cheek_width, 0, 200, idPaint);
        canvas.drawText("Forehead Width = " + forehead_width, 0, 250, idPaint);

        canvas.drawText("Face Height = " + face_height, 0, 150, idPaint);
        canvas.drawText("chin width = " + chin_width, 0, 400, idPaint);
        //calculate distance in mm
        float distance = calculateFaceToCameraDistance();
        canvas.drawText("distance = " + distance, 0, 600, idPaint);
        if (distance >= 140 && distance <= 150) {
            calculateWidestPart(canvas, jaw_width, cheek_width, forehead_width, face_height);
            calculateFaceLength(canvas, jaw_width, cheek_width, forehead_width, face_height, chin_width);

            calculateRightEyebrowCoordinates(canvas);
            calculateLeftEyebrowCoordinates(canvas);
            calculateLeftCheekCoordinates(canvas);
            calculateRightCheekCoordinates(canvas, right_nose_to_cheek_width);
            calculateLipsCoordinates(canvas);
        }
        //}


//    DisplayMetrics mDisplayMetric = this.getApplicationContext().getResources().getDisplayMetrics();
//    float anyPixel = (contour.getPoints().get(12).getX()/2.0f) - (contour.getPoints().get(24).getX()/2.0f);
//    float widthInches =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, anyPixel, mDisplayMetric);
//
//    canvas.drawText("Jaw Width = " + widthInches, 0, 150, idPaint);


//    if (face.getSmilingProbability() >= 0) {
//      canvas.drawText(
//          "happiness: " + String.format("%.2f", face.getSmilingProbability()),
//          x + ID_X_OFFSET * 3,
//          y - ID_Y_OFFSET,
//          idPaint);
//    }
//
//    if (face.getRightEyeOpenProbability() >= 0) {
//      canvas.drawText(
//          "right eye: " + String.format("%.2f", face.getRightEyeOpenProbability()),
//          x - ID_X_OFFSET,
//          y,
//          idPaint);
//    }
//    if (face.getLeftEyeOpenProbability() >= 0) {
//      canvas.drawText(
//          "left eye: " + String.format("%.2f", face.getLeftEyeOpenProbability()),
//          x + ID_X_OFFSET * 6,
//          y,
//          idPaint);
//    }


        FirebaseVisionFaceLandmark leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
        if (leftEye != null && leftEye.getPosition() != null) {
            canvas.drawCircle(
                    translateX(leftEye.getPosition().getX()),
                    translateY(leftEye.getPosition().getY()),
                    FACE_POSITION_RADIUS,
                    facePositionPaint);
        }
//    FirebaseVisionFaceLandmark rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE);
//    if (rightEye != null && rightEye.getPosition() != null) {
//      canvas.drawCircle(
//          translateX(rightEye.getPosition().getX()),
//          translateY(rightEye.getPosition().getY()),
//          FACE_POSITION_RADIUS,
//          facePositionPaint);
//    }
//
//    FirebaseVisionFaceLandmark leftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK);
//    if (leftCheek != null && leftCheek.getPosition() != null) {
//      canvas.drawCircle(
//          translateX(leftCheek.getPosition().getX()),
//          translateY(leftCheek.getPosition().getY()),
//          FACE_POSITION_RADIUS,
//          facePositionPaint);
//    }
//    FirebaseVisionFaceLandmark rightCheek = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK);
//    if (rightCheek != null && rightCheek.getPosition() != null) {
//      canvas.drawCircle(
//          translateX(rightCheek.getPosition().getX()),
//          translateY(rightCheek.getPosition().getY()),
//          FACE_POSITION_RADIUS,
//          facePositionPaint);
//    }
    }

    private float calculateFaceToCameraDistance() {
        float d = 0f;
        float p = (float) Math.sqrt(
                (Math.pow((contour.getPoints().get(60).getX() -
                        contour.getPoints().get(76).getX()), 2) +
                        Math.pow((contour.getPoints().get(60).getY() -
                                contour.getPoints().get(76).getY()), 2)));
        /*Toast.makeText(context, String.valueOf(p), Toast.LENGTH_LONG).show();
        Toast.makeText(context, String.valueOf(cameraSource.getFocalLength()), Toast.LENGTH_LONG).show();*/
        if (cameraSource != null) {
            float H = 63;
            d = cameraSource.getFocalLength() * (H / cameraSource.sensorX) * (768 / (2 * p));


        }
        return d;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateLipsCoordinates(Canvas canvas) {
        PointF leftLipsEdge = new PointF(translateX(contour.getPoints().get(98).getX()),
                translateY(contour.getPoints().get(98).getY()));
        PointF rightLipsEdge = new PointF(translateX(contour.getPoints().get(93).getX()),
                translateY(contour.getPoints().get(93).getY()));
        drawEyebrow(canvas, leftLipsEdge, rightLipsEdge, R.drawable.lip);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateRightCheekCoordinates(Canvas canvas, float right_nose_to_cheek_width) {
        PointF cheekPositionRight = new PointF(translateX(contour.getPoints().get(128).getX()),
                translateY(contour.getPoints().get(128).getY()));

        PointF cheekPositionTop = new PointF(translateX(contour.getPoints().get(29).getX()),
                translateY(contour.getPoints().get(29).getY()));
        drawEyebrow(canvas, cheekPositionRight, cheekPositionTop, R.drawable.right_cheek);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateLeftCheekCoordinates(Canvas canvas) {
        PointF cheekPositionLeft = new PointF(translateX(contour.getPoints().get(7).getX()),
                translateY(contour.getPoints().get(7).getY()));

        PointF cheekPositionTop = new PointF(translateX(contour.getPoints().get(7).getX()),
                translateY(contour.getPoints().get(7).getY()));
        drawEyebrow(canvas, cheekPositionLeft, cheekPositionTop, R.drawable.left_cheek);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateLeftEyebrowCoordinates(Canvas canvas) {
        PointF eyebrowPositionRight = new PointF(translateX(contour.getPoints().get(46).getX()),
                translateY(contour.getPoints().get(46).getY()));

        PointF eyebrowPositionTop = new PointF(translateX(contour.getPoints().get(48).getX()),
                translateY(contour.getPoints().get(48).getY()));
        drawEyebrow(canvas, eyebrowPositionRight, eyebrowPositionTop, R.drawable.eyebrow_left);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateRightEyebrowCoordinates(Canvas canvas) {
        PointF eyebrowPositionLeft = new PointF(translateX(contour.getPoints().get(36).getX()),
                translateY(contour.getPoints().get(36).getY()));

        PointF eyebrowPositionRight = new PointF(translateX(contour.getPoints().get(40).getX()),
                translateY(contour.getPoints().get(40).getY()));

        PointF eyebrowPositionTop = new PointF(translateX(contour.getPoints().get(38).getX()),
                translateY(contour.getPoints().get(38).getY()));

        PointF eyebrowPositionBottom = new PointF(translateX(contour.getPoints().get(43).getX()),
                translateY(contour.getPoints().get(43).getY()));
        drawEyebrow(canvas, eyebrowPositionRight, eyebrowPositionTop, R.drawable.eyebrow_right);
    }

    private void calculateFaceLength(Canvas canvas, float jaw_width, float cheek_width, float forehead_width, float face_height, float chin_width) {
        face_height = Math.round(face_height);
        cheek_width = Math.round(cheek_width);
        forehead_width = Math.round(forehead_width);
        jaw_width = Math.round(jaw_width);
        int jaw_forehead_difference = (int) Math.abs((jaw_width - forehead_width));
        int cheek_jaw_difference = (int) Math.abs((cheek_width - jaw_width));
        int cheek_forehead_difference = (int) Math.abs((cheek_width - forehead_width));
        if (face_height >= cheek_width && jaw_forehead_difference <= 10) {
            canvas.drawText("Face Shape = Round" + " ", 0, 450, idPaint);
        } else if (face_height > cheek_width && cheek_jaw_difference > 10) {
            canvas.drawText("Face Shape = Oval " + " ", 0, 450, idPaint);
        } else if (cheek_jaw_difference <= 5 && cheek_forehead_difference <= 5) {
            canvas.drawText("Face Shape = Square" + " ", 0, 450, idPaint);
        } else if (face_height > cheek_width && forehead_width > jaw_width && chin_width < 30) {
            canvas.drawText("Face Shape = Diamond" + " ", 0, 450, idPaint);
        }
    }

    private void calculateWidestPart(Canvas canvas, float jaw_width, float cheek_width, float forehead_width, float face_height) {
        if (forehead_width > jaw_width && forehead_width > cheek_width) {
            canvas.drawText("widest part = Forehead " + " ", 0, 300, idPaint);
        } else if (jaw_width > forehead_width && jaw_width > cheek_width) {
            canvas.drawText("widest part = Jaw" + " ", 0, 300, idPaint);
        } else {
            canvas.drawText("widest part = right_cheek" + " ", 0, 300, idPaint);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawEyebrow(Canvas canvas,
                             PointF rightEyebrow, PointF topEyebrow, int eyebrows) {
        //int right = (int)leftEyebrow.x;
        int left = (int) rightEyebrow.x;
        int top = (int) topEyebrow.y;
        //int bottom = (int)bottomEyebrow.y;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), eyebrows);
        //if you are in non activity then use context.getResources()
        canvas.drawBitmap(bitmap, left, top, facePositionPaint);
    }
}
