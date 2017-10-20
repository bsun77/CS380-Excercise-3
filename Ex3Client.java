import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Ex3Client {

	public static void main(String[] args) throws Exception {
		try (Socket socket = new Socket("18.221.102.182", 38103)) {
			System.out.println("Connected to server.");

			InputStream is = socket.getInputStream();
			int a = is.read();
			byte[] stuff = new byte[a];
			
			
			System.out.println("Reading " +a +" bytes.");
			System.out.println("Data received:");
			System.out.print("  ");
			for(int i = 0; i < a; i++){
				stuff[i] = (byte)is.read();
				if(i%10==0&&i!=0){
					System.out.println();
					System.out.print("  ");
				}
				System.out.print(String.format("%02x", stuff[i]).toUpperCase());
			}
			
			short little = checksum(stuff);
			byte[] checksum = new byte[2];
			checksum[0] = (byte) (little >>> 8);
			checksum[1] = (byte) (little & 0xFF);
			
			System.out.println();
			OutputStream os = socket.getOutputStream();
			os.write(checksum);
			if(is.read() == 1){
				System.out.println("Response good.");
			} else {
				System.out.println("Response bad.");
			}
			System.out.println("Disconnected from server");
		}
	}
	
	public static short checksum(byte[] b){
		int num = b.length % 2 == 0 ? b.length/2 : b.length/2+1;
		int[] lessstuff = new int[num];
		for(int i = 0; i < lessstuff.length; i++){
			int temp = (b[2*i]&0xFF) << 8;
			int x = 0;
			try {
				x = (b[2*i+1]&0xFF);
			} catch (ArrayIndexOutOfBoundsException e){}
			lessstuff[i] = temp+x;
		}
		
		long sum = 0;
		for(int i = 0; i < lessstuff.length; i++){
			sum += lessstuff[i];
			if((sum & 0xFFFF0000) != 0){
				sum &= 0xFFFF;
				sum++;
			}
		}
		return (short) ~(sum & 0xFFFF);
	}
	
}