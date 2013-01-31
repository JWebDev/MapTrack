package de.ai.mi.maptrack.src;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class RouteOverlay extends Overlay {

	private List<Location> locations;
	private Paint pathPaint;
	private Paint positionPaint;
	private final int POSITION_MARKER = 10;

	public RouteOverlay() {

		pathPaint = new Paint();
		pathPaint.setAntiAlias(true);
		pathPaint.setColor(Color.RED);
		pathPaint.setStyle(Paint.Style.STROKE);
		pathPaint.setStrokeWidth(5);
		locations = new ArrayList<Location>(); // initialize points

		positionPaint = new Paint();
		positionPaint.setAntiAlias(true);
		positionPaint.setStyle(Paint.Style.FILL);
	}

	public void addPoint(Location location) {
		locations.add(location);
	}

	public void reset() {
		locations.clear();
	}

	// рисование слоя Overlay в верхней части MapView
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow); // вызов метода
		// draw суперкласса
		Path newPath = new Path(); // получение нового объекта Path
		Location previous = null; // инициализация предыдущей локации нулем
		// для каждой локации
		for (int i = 0; i < locations.size(); ++i) {
			Location location = locations.get(i);
			// преобразование Location в GeoPoint
			Double newLatitude = location.getLatitude() * 1E6;
			Double newLongitude = location.getLongitude() * 1E6;
			GeoPoint newPoint = new GeoPoint(newLatitude.intValue(), newLongitude.intValue());
			// преобразование GeoPoint в точку на экране
			Point newScreenPoints = new Point();
			mapView.getProjection().toPixels(newPoint, newScreenPoints);
			if (previous != null) // если это не первая локация
			{
				// получение GeoPoint для предыдущей локации
				Double oldLatitude = previous.getLatitude() * 1E6;
				Double oldLongitude = previous.getLongitude() * 1E6;
				GeoPoint oldPoint = new GeoPoint(oldLatitude.intValue(), oldLongitude.intValue());
				// преобразование GeoPoint в точку на экране
				Point oldScreenPoints = new Point();
				mapView.getProjection().toPixels(oldPoint, oldScreenPoints);
				// добавление новой точки в объект Path
				newPath.quadTo(oldScreenPoints.x, oldScreenPoints.y, (newScreenPoints.x + oldScreenPoints.x) / 2, (newScreenPoints.y + oldScreenPoints.y) / 2);
				// возможно рисование черной точки для текущей позиции
				if ((i % POSITION_MARKER) == 0)
					canvas.drawCircle(newScreenPoints.x, newScreenPoints.y, 10, positionPaint);
			} // конец блока if
			else {
				// перемещение к первой локации
				newPath.moveTo(newScreenPoints.x, newScreenPoints.y);
			} // конец блока else
			previous = location; // хранение локации
		}
		// конец цикла for
		canvas.drawPath(newPath, pathPaint); // рисование контура
	} // конец описания метода draw

}
