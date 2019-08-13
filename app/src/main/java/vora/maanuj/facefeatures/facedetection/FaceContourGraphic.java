package vora.maanuj.facefeatures.facedetection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.google.android.gms.vision.face.Face;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import vora.maanuj.facefeatures.common.GraphicOverlay;

/** Graphic instance for rendering face contours graphic overlay view. */
public class FaceContourGraphic extends GraphicOverlay.Graphic {

  private static final float FACE_POSITION_RADIUS = 4.0f;
  private static final float ID_TEXT_SIZE = 30.0f;
  private static final float ID_Y_OFFSET = 80.0f;
  private static final float ID_X_OFFSET = -70.0f;
  private static final float BOX_STROKE_WIDTH = 5.0f;

  private final Paint facePositionPaint;
  private final Paint idPaint;
  private final Paint boxPaint;

  private volatile FirebaseVisionFace firebaseVisionFace;

  private float[] leftx = new float[4];
  private float[] rightx = new float[4];
  private float[] lefty = new float[4];
  private float[] righty = new float[4];

  public FaceContourGraphic(GraphicOverlay overlay, FirebaseVisionFace face) {
    super(overlay);

    this.firebaseVisionFace = face;
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

  /** Draws the face annotations for position on the supplied canvas. */
  @Override
  public void draw(Canvas canvas) {
    FirebaseVisionFace face = firebaseVisionFace;
    if (face == null) {
      return;
    }

//    // Draws a circle at the position of the detected face, with the face's track id below.
//    float x = translateX(face.getBoundingBox().centerX());
//    float y = translateY(face.getBoundingBox().centerY());
//    canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint);
//    canvas.drawText("id: " + face.getTrackingId(), x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint);

//    // Draws a bounding box around the face.
//    float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
//    float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
//    float left = x - xOffset;
//    float top = y - yOffset;
//    float right = x + xOffset;
//    float bottom = y + yOffset;
//    canvas.drawRect(left, top, right, bottom, boxPaint);


//    canvas.drawText("Width = " + face.getBoundingBox().width(), 0, 50, idPaint);
//    canvas.drawText("Height = " + face.getBoundingBox().height(), 0, 100, idPaint);


    FirebaseVisionFaceContour contour = face.getContour(FirebaseVisionFaceContour.ALL_POINTS);



//    for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : contour.getPoints()) {
//      float px = translateX(point.getX());
//      float py = translateY(point.getY());
//      canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);
//    }

    for(int z = 0; z < contour.getPoints().size(); z++){
      if(z < 36) {
        if (z == 35) {
          canvas.drawLine(translateX(contour.getPoints().get(z).getX()), translateY(contour.getPoints().get(z).getY()), translateX(contour.getPoints().get(0).getX()), translateY(contour.getPoints().get(0).getY()), idPaint);
        } else {
          canvas.drawLine(translateX(contour.getPoints().get(z).getX()), translateY(contour.getPoints().get(z).getY()), translateX(contour.getPoints().get(z + 1).getX()), translateY(contour.getPoints().get(z + 1).getY()), idPaint);
        }
      }
    }

    canvas.drawLine(translateX(contour.getPoints().get(33).getX()), translateY(contour.getPoints().get(33).getY()), translateX(contour.getPoints().get(3).getX()), translateY(contour.getPoints().get(3).getY()), idPaint);
    canvas.drawLine(translateX(contour.getPoints().get(12).getX()), translateY(contour.getPoints().get(12).getY()), translateX(contour.getPoints().get(24).getX()), translateY(contour.getPoints().get(24).getY()), idPaint);
    canvas.drawLine(translateX(contour.getPoints().get(10).getX()), translateY(contour.getPoints().get(10).getY()), translateX(contour.getPoints().get(26).getX()), translateY(contour.getPoints().get(26).getY()), idPaint);
    canvas.drawLine(translateX(contour.getPoints().get(18).getX()), translateY(contour.getPoints().get(18).getY()), translateX(contour.getPoints().get(0).getX()), translateY(contour.getPoints().get(0).getY()), idPaint);


    // Text Displayed

    canvas.drawText("Jaw Width = " + ((contour.getPoints().get(12).getX()/2.0f) - (contour.getPoints().get(24).getX()/2.0f)), 0, 100, idPaint);
    canvas.drawText("Cheek Width = " + ((contour.getPoints().get(10).getX()/2.0f) - (contour.getPoints().get(26).getX()/2.0f)), 0, 200, idPaint);
    canvas.drawText("Forehead Width = " + ((contour.getPoints().get(3).getX()/2.0f) - (contour.getPoints().get(33).getX()/2.0f)), 0, 250, idPaint);

    canvas.drawText("Face Height = " + ((contour.getPoints().get(18).getY()/2.0f) - (contour.getPoints().get(0).getY()/2.0f)), 0, 150, idPaint);

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




//    FirebaseVisionFaceLandmark leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
//    if (leftEye != null && leftEye.getPosition() != null) {
//      canvas.drawCircle(
//          translateX(leftEye.getPosition().getX()),
//          translateY(leftEye.getPosition().getY()),
//          FACE_POSITION_RADIUS,
//          facePositionPaint);
//    }
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
}
