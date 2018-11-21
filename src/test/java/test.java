import java.util.Arrays;
public class test {
	public static void main(String[] args) {
		int num = 4;//循环次数
		int preNum = num-1;//前一个数，比如b<a 则a为b的前一个数
		int[] array = new int[num];//数组保存每次循环后dcba的值。
		loop(num, preNum, array);
	}
	private static void loop(int num, int preNum, int[] array) {
		//为0则循环次数到了，开始打印
		if(num == 0) {
			for(int i = array.length; i > 0; i--) {
				System.out.print(array[i-1]+" ");
			}
			System.out.println();
			return;
		}
		//i为a,b,c,d中的一个，小于前一个数则保存后进入下一个循环。
		for(int currentNum = 0; currentNum <= preNum; currentNum++) {
			array[num-1] = currentNum;//保存当前数
			loop(num-1, currentNum, array);
		}
		return;
	}
}