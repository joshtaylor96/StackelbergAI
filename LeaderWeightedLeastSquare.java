import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * A pseudo leader. The members m_platformStub and m_type are declared in the
 * PlayerImpl, and feel free to use them. You may want to check the
 * implementation of the PlayerImpl. You will use m_platformStub to access the
 * platform by calling the remote method provided by it.
 * 
 * @author Xin
 */
final class LeaderWeightedLeastSquare extends PlayerImpl {
	/**
	 * In the constructor, you need to call the constructor of PlayerImpl in the
	 * first line, so that you don't need to care about how to connect to the
	 * platform. You may want to throw the two exceptions declared in the prototype,
	 * or you may handle it by using "try {} catch {}". It's all up to you.
	 * 
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private ArrayList<Record> historicalData;
	private float a;
	private float b;
	private float forgettingFactor = (float) 0.99;

	LeaderWeightedLeastSquare() throws RemoteException, NotBoundException {
		super(PlayerType.LEADER, "Group29Leader");
		historicalData = new ArrayList<Record>();
		a = (float) 0;
		b = (float) 0;
	}

	/**
	 * You may want to delete this method if you don't want modify the original
	 * connection checking behavior of the platform. Actually I recommend you to
	 * delete this method from your own code
	 * 
	 * @throws RemoteException If implemented, the RemoteException *MUST* be thrown
	 *                         by this method
	 */
	@Override
	public void checkConnection() throws RemoteException {
		super.checkConnection();
		// TO DO: delete the line above and put your own code here
	}

	/**
	 * You may want to delete this method if you don't want the platform to control
	 * the exit behavior of your leader class
	 * 
	 * @throws RemoteException If implemented, the RemoteException *MUST* be thrown
	 *                         by this method
	 */
	@Override
	public void goodbye() throws RemoteException {
		super.goodbye();
		// TO DO: delete the line above and put your own exit code here
	}

	/**
	 * You may want to delete this method if you don't want to do any initialization
	 * 
	 * @param p_steps Indicates how many steps will the simulation perform
	 * @throws RemoteException If implemented, the RemoteException *MUST* be thrown
	 *                         by this method
	 */
	@Override
	public void startSimulation(int p_steps) throws RemoteException {
		// Reset
		System.out.println("==========================");
		System.out.println("New simulation");
		historicalData.clear();
		m_platformStub.log(m_type, "startSimulation()");
		Record currentRecord;
		for (int i = 1; i <= 100; i++) {
			historicalData.add(m_platformStub.query(m_type, i));
			// leaderPrices.add(currentRecord.m_leaderPrice);
			// followerPrices.add(currentRecord.m_followerPrice);
			// followerCosts.add(currentRecord.m_cost);
		}
	}

	/**
	 * You may want to delete this method if you don't want to do any finalization
	 * 
	 * @throws RemoteException If implemented, the RemoteException *MUST* be thrown
	 *                         by this method
	 */
	@Override
	public void endSimulation() throws RemoteException {
		super.endSimulation();
		// TO DO: delete the line above and put your own finalization code here
	}

	/**
	 * To inform this instance to proceed to a new simulation day
	 * 
	 * @param p_date The date of the new day
	 * @throws RemoteException This exception *MUST* be thrown by this method
	 */
	@Override
	public void proceedNewDay(int p_date) throws RemoteException {
		m_platformStub.log(m_type, "proceedNewDay()");
		checkHistory(p_date);
		regressionEquationCalc(p_date);
		m_platformStub.publishPrice(m_type, globalMaxCalc());
		historicalData.add(m_platformStub.query(m_type, p_date));
	}

	private void checkHistory(int p_date) throws RemoteException {
		if (p_date - 1 != historicalData.size()) {
			System.out.println("Correction needed");
			reloadHistory(p_date);
		} else {
			System.out.println("No correction needed");
		}
	}

	private void reloadHistory(int p_date) throws RemoteException {
		historicalData.clear();
		Record currentRecord;
		for (int i = 1; i < p_date; i++) {
			historicalData.add(m_platformStub.query(m_type, i));
		}
	}

	private void regressionEquationCalc(int currentDate) throws RemoteException {
		float xSum = 0;
		float xSumSquare = 0;
		float ySum = 0;
		float xSum_ySum = 0;
		float lambda;
		int T = currentDate - 1;
		Record currentRecord;

		for (int i = 0; i < currentDate - 1; i++) {
			lambda = (float) Math.pow(forgettingFactor, T - i);
			currentRecord = historicalData.get(i);
			xSum = xSum + lambda * currentRecord.m_leaderPrice;
			ySum = ySum + lambda * currentRecord.m_followerPrice;
			xSumSquare = xSumSquare + lambda * currentRecord.m_leaderPrice * currentRecord.m_leaderPrice;
			xSum_ySum = xSum_ySum + lambda * currentRecord.m_leaderPrice * currentRecord.m_followerPrice;
		}
		a = (xSumSquare * ySum - xSum * xSum_ySum) / (((float) T) * xSumSquare - (float) Math.pow(xSum, 2));
		b = (((float) T) * xSum_ySum - xSum * ySum) / (((float) T) * xSumSquare - (float) Math.pow(xSum, 2));
		m_platformStub.log(m_type, "a: " + a + ",b: " + b);
		System.out.println("a: " + a + ",b: " + b);
	}

	private float globalMaxCalc() {
		// return (((float)3.0 + (float)0.3 *a- (float)0.3*b)/((float)2.0
		// -(float)0.6*b));
		return ((float) 3 * b - (float) 3 * a - 30) / ((float) 6 * b - 20);
	}

	public static void main(final String[] p_args) throws RemoteException, NotBoundException {
		new LeaderWeightedLeastSquare();
	}
}
