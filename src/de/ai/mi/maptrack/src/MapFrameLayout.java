package de.ai.mi.maptrack.src;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.maps.MapView;

public class MapFrameLayout extends FrameLayout {

	private int scale = 0;
	private MapView mapView;
	private float bearing = 0f;

	public LayoutParams getChildLayoutParams() {
		Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		scale = (int) Math.sqrt((width * width) + (height * height));
		return new LayoutParams(scale, scale);
	}

	// общедоступный конструктор класса BearingFrameLayout
	public MapFrameLayout(Context context, String apiKey) {
		super(context); // вызов конструктора суперкласса
		mapView = new MapView(context, apiKey); // создание нового MapView
		mapView.setClickable(true); // обеспечение взаимодействия
		// пользователя с картой
		mapView.setEnabled(true); // активизация генерирования событий
		// компонентом MapView
		mapView.setSatellite(false); // отображение традиционной карты
		mapView.setBuiltInZoomControls(true); // активизация элементов
		// управления масштабом
		// настройка макета MapView
		mapView.setLayoutParams(getChildLayoutParams());
		addView(mapView); // добавление MapView в макет
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (bearing >= 0) // если значение bearing больше 0
		{
			// получение размеров холста
			int canvasWidth = canvas.getWidth();
			int canvasHeight = canvas.getHeight();
			// размеры масштабированного холста
			int width = scale;
			int height = scale;
			// центрирование масштабированного холста
			int centerXScaled = width / 2;
			int centerYScaled = height / 2;

			// центр холста на экране
			int centerX = canvasWidth / 2;
			int centerY = canvasHeight / 2;
			// перемещение центра масштабированной области в фактический
			// центр экрана
			canvas.translate(-(centerXScaled - centerX), -(centerYScaled - centerY));
			// вращение вокруг центра экрана
			canvas.rotate(-bearing, centerXScaled, centerYScaled);
		} // конец блока if
		super.dispatchDraw(canvas); // рисование дочерних
		// представлений для данного макета
	}

	// настройка компаса
	public void setBearing(float bearing) {
		this.bearing = bearing;
	} // конец определения метода setBearing
		// возврат MapView

	public MapView getMapView() {
		return mapView;
	} // конец определения метода getMapView

}
