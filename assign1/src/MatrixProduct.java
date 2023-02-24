import java.util.Scanner; 

public class MatrixProduct{
    public static void main(String args[]){
        int op, lin , col;
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Multiplication");
		System.out.println("2. Line Multiplication");
		System.out.println("Selection?: ");

        op = sc.nextInt();

        System.out.println("Dimensions: lins=cols ? ");

        lin = sc.nextInt();
        
        col = lin;

        if ( op == 1 )
            OnMult(lin, col);
        else
            OnMultLine(lin, col);
    }

    public static void OnMult(int m_ar, int m_br) {
		long time1, time2;

		double temp;
		int i, j, k;

		double[] pha = new double[m_ar * m_ar];
		double[] phb = new double[m_ar * m_ar];
		double[] phc = new double[m_ar * m_ar];

		for(i = 0; i < m_ar; i++)
			for(j = 0; j < m_ar; j++)
				pha[i * m_ar + j] = (double)1.0; //type conversion stuff

		for(i = 0; i < m_br; i++)
			for(j = 0; j < m_br; j++)
				phb[i * m_br + j] = (double)(i+1);

		time1 = System.currentTimeMillis();

		for(i = 0; i < m_ar; i++){
			for(j = 0; j < m_br; j++){
				temp = 0;
				for( k = 0; k < m_ar; k++){	
					temp += pha[i * m_ar + k] * phb[k * m_br +j];
				}
				phc[i * m_ar + j] = temp;
			}
		}

		time2 = System.currentTimeMillis();

		System.out.printf("Time: %3.3f seconds\n", (double)(time2-time1)/1000);
	}

	public static void OnMultLine(int m_ar, int m_br) {
		long time1, time2;

		int i, j, k;

		double[] pha = new double[m_ar * m_ar];
		double[] phb = new double[m_ar * m_ar];
		double[] phc = new double[m_ar * m_ar];

		for(i = 0; i < m_ar; i++)
			for(j = 0; j < m_ar; j++)
				pha[i * m_ar + j] = (double)1.0;

		for(i = 0; i < m_br; i++)
			for(j = 0; j < m_br; j++)
				phb[i * m_br + j] = (double)(i+1);

		for (i = 0; i < m_ar; i++)
			for (j = 0; j < m_ar; j++)
				phc[i * m_ar + j] = (double)0.0;
		
		time1 = System.currentTimeMillis();

		for (i = 0; i < m_ar; i++) {
			for (k = 0; k < m_br; k++) {
				for (j = 0; j < m_ar; j++) {
					phc[i * m_ar + j] += pha[i * m_ar + k] * phb[k * m_br + j];
				}
			}
		}

		time2 = System.currentTimeMillis();

		System.out.printf("Time: %3.3f seconds\n", (double)(time2-time1)/1000);
	}
}