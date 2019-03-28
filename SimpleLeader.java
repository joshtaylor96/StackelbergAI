import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

/**
 * A very simple leader implementation that only generates random prices
 * @author Xin
 */
final class SimpleLeader
	extends PlayerImpl
{
	/* The randomizer used to generate random price */
	private final Random m_randomizer = new Random(System.currentTimeMillis());
	private ArrayList<Float> leaderPrices;
	private ArrayList<Float> followerPrices;
	private ArrayList<Float> followerCosts;
	private float a;
	private float b;

	private SimpleLeader()
		throws RemoteException, NotBoundException
	{
		super(PlayerType.LEADER, "Simple Leader");
		leaderPrices = new ArrayList<Float>();
		followerPrices = new ArrayList<Float>();
		followerCosts = new ArrayList<Float>();
		a = 0;
		b = 0;
	}

	@Override
	public void goodbye()
		throws RemoteException
	{
		ExitTask.exit(500);
	}

	/**
	 * To inform this instance the start of the simulation
	 * @param p_steps Indicates how many steps will this round of simulation perform
	 * @throws RemoteException
	 */
	@Override
	public void startSimulation(int p_steps)
		throws RemoteException
	{
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
	 * To inform this instance to proceed to a new simulation day
	 * @param p_date The date of the new day
	 * @throws RemoteException
	 */
	@Override
	public void proceedNewDay(int p_date)
		throws RemoteException
	{
		m_platformStub.publishPrice(m_type, genPrice());
	}

	/**
	 * Generate a random price based Gaussian distribution. The mean is p_mean,
	 * and the diversity is p_diversity
	 * @param p_mean The mean of the Gaussian distribution
	 * @param p_diversity The diversity of the Gaussian distribution
	 * @return The generated price
	 */
	private float genPrice() throws RemoteException
	{
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
		new SimpleLeader();
	}

	/**
	 * The task used to automatically exit the leader process
	 * @author Xin
	 */
	private static class ExitTask
		extends TimerTask
	{
		static void exit(final long p_delay)
		{
			(new Timer()).schedule(new ExitTask(), p_delay);
		}

		@Override
		public void run()
		{
			System.exit(0);
		}
	}
}
