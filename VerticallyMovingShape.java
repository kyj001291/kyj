import java.net.URL;



public class VerticallyMovingShape extends Shape {
	public VerticallyMovingShape(URL imgURL, int x, int y, int margin, int steps, int xBoundary, int yBoundary) {
		// imgPath : �׸� ������ ��θ�
		// x, y : �̹����� ��ġ ��ǥ
		// margin : �� �̹����� ������ ��Ÿ���� ���� (�� �����ȿ� ������ �浹 �� ������ �Ǵ� �ϱ� ����)
		// steps : �̹����� �����϶� �̵��ϴ� ��ǥ ����
		// xBoundary, yBoundary : �׸��� �̵��� �� �ִ� ��ǥ�� �ִ밪
		super (imgURL, x, y, margin, steps, xBoundary, yBoundary);
	}
	
	public VerticallyMovingShape(URL imgURL, int margin, int steps, int xBoundary, int yBoundary) {
		super (imgURL, margin, steps, xBoundary, yBoundary);
	}

	public void move() {
		
		if (yDirection > 0 && y >= yBoundary) {
			yDirection = 1;
		}
		
		y += (yDirection * steps);
	}
	public void rMove() {
		yDirection=-2;
		y+=(yDirection * steps);
	}
}
