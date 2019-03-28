import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * A pseudo leader. The members m_platformStub and m_type are declared
 * in the PlayerImpl, and feel free to use them. You may want to check
 * the implementation of the PlayerImpl. You will use m_platformStub to access
 * the platform by calling the remote method provided by it.
 * @author Xin
 */
final class Leader
	extends PlayerImpl
{
	/**
	 * In the constructor, you need to call the constructor
	 * of PlayerImpl in the first line, so that you don't need to
	 * care about how to connect to the platform. You may want to throw
	 * the two exceptions declared in the prototype, or you may handle it
	 * by using "try {} catch {}". It's all up to you.
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
 		private ArrayList<Float> leaderPrices;
 		private ArrayList<Float> followerPrices;
 		private ArrayList<Float> followerCosts;
 		private float a;
 		private float b;
	Leader()
		throws RemoteException, NotBoundException
	{
		/* The first parameter *MUST* be PlayerType.LEADER, you can change
		 * the second parameter, the name of the leader, such as "My Leader" */
		 super(PlayerType.LEADER, "Group29Leader");
		 leaderPrices = new ArrayList<Float>();
		 followerPrices = new ArrayList<Float>();
		 followerCosts = new ArrayList<Float>();
		 a = (float) 0;
		 b = (float) 0;
	}

	/**
	 * You may want to delete this method if you don't want modify
	 * the original connection checking behavior of the platform.
	 * Actually I recommend you to delete this method from your own code
	 * @throws RemoteException If implemented, the RemoteException *MUST* be
	 * thrown by this method
	 */
	@Override
	public void checkConnection()
		throws RemoteException
	{
		super.checkConnection();
		//TO DO: delete the line above and put your own code here
	}

	/**
	 * You may want to delete this method if you don't want the platform
	 * to control the exit behavior of your leader class
	 * @throws RemoteException If implemented, the RemoteException *MUST* be
	 * thrown by this method
	 */
	@Override
	public void goodbye()
		throws RemoteException
	{
		super.goodbye();
		//TO DO: delete the line above and put your own exit code here
	}

	/**
	 * You may want to delete this method if you don't want to do any
	 * initialization
	 * @param p_steps Indicates how many steps will the simulation perform
	 * @throws RemoteException If implemented, the RemoteException *MUST* be
	 * thrown by this method
	 */
	 @Override
 	public void startSimulation(int p_steps) throws RemoteException {
 		// Reset
 		leaderPrices = new ArrayList<Float>();
 		followerPrices = new ArrayList<Float>();
 		followerCosts = new ArrayList<Float>();
 		m_platformStub.log(m_type, "startSimulation()");
 		Record currentRecord;
 		for (int i = 1; i <= 100; i++) {
 			currentRecord = m_platformStub.query(m_type, i);
 			leaderPrices.add(currentRecord.m_leaderPrice);
 			followerPrices.add(currentRecord.m_followerPrice);
 			followerCosts.add(currentRecord.m_cost);
 		}
 		//leaderPrices.add((float) 3.0); leaderPrices.add((float) 4.0); leaderPrices.add((float) 5.0); leaderPrices.add((float) 6.0); leaderPrices.add((float) 7.0);
 		//followerPrices.add((float) 2.0); followerPrices.add((float) 3.0); followerPrices.add((float) 3.0); followerPrices.add((float) 4.0); followerPrices.add((float) 6.0);
 		float aUpperFirst = 0;
 		float aUpperSecond = 0;
 		float aUpperThird = 0;
 		float aUpperFourth = 0;
 		float aLowerFirst = 0;
 		float aLowerSecond = 0;
 		for (int i = 0; i < leaderPrices.size(); i++) {
 			aUpperFirst = aUpperFirst + leaderPrices.get(i)*leaderPrices.get(i);
 			aUpperSecond = aUpperSecond + followerPrices.get(i);
 			aUpperThird = aUpperThird + leaderPrices.get(i);
 			aUpperFourth = aUpperFourth + leaderPrices.get(i)*followerPrices.get(i);
 			aLowerFirst = aLowerFirst + leaderPrices.get(i)*leaderPrices.get(i);
 			aLowerSecond = aLowerSecond + leaderPrices.get(i);
 		}
 		a = (aUpperFirst*aUpperSecond-aUpperThird*aUpperFourth)/(((float) leaderPrices.size())*aLowerFirst - aLowerSecond*aLowerSecond);
 		b = (((float) leaderPrices.size())*aUpperFourth-aUpperThird*aUpperSecond)/(((float) leaderPrices.size())*aUpperFirst-aLowerSecond*aLowerSecond);
 		m_platformStub.log(m_type, "a: " + a + ",b: " + b);
	}

	/**
	 * You may want to delete this method if you don't want to do any
	 * finalization
	 * @throws RemoteException If implemented, the RemoteException *MUST* be
	 * thrown by this method
	 */
	@Override
	public void endSimulation()
		throws RemoteException
	{
		super.endSimulation();
		//TO DO: delete the line above and put your own finalization code here
	}

	/**
	 * To inform this instance to proceed to a new simulation day
	 * @param p_date The date of the new day
	 * @throws RemoteException This exception *MUST* be thrown by this method
	 */
	@Override
	public void proceedNewDay(int p_date)
		throws RemoteException
	{
		/*
		 * Check for new price
		 * Record l_newRecord = m_platformStub.query(m_type, p_date);
		 *
		 * Your own math model to compute the price here
		 * ...
		 * float l_newPrice = ....
		 *
		 * Submit your new price, and end your phase
		 * m_platformStub.publishPrice(m_type, l_newPrice);
		 */
		 m_platformStub.publishPrice(m_type, 1);
	}

	private float genPrice() throws RemoteException {
		//return (float) (p_mean + m_randomizer.nextGaussian() * p_diversity);
		float bestStrat = ((float) 1);
		float bestSales = ((float) 2) - bestStrat + ((float) 0.3)*(a+b*bestStrat);
		float bestProfit = (bestStrat-1)*bestSales;
		float currentSales;
		float currentProfit;
		int loops = 0;
		for (float currentStrat = ((float) 1.00); currentStrat < ((float) 3); currentStrat = currentStrat + ((float) 0.01)) {
			currentSales = ((float) 2) - currentStrat + ((float) 0.3)*(a+b*currentStrat);
			currentProfit = (currentStrat-1)*currentSales;
			if (currentProfit > bestProfit) {
				bestStrat = currentStrat;
				bestProfit = currentProfit;
			}
			m_platformStub.log(m_type, "strat: " + currentStrat + ", profit: " + currentProfit);
	  }
		return bestStrat;
	}

	public static void main(final String[] p_args)
		throws RemoteException, NotBoundException
	{
		new Leader();
	}
}
