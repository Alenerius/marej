package se.marej.atm.service;

import se.marej.atm.exception.ATMException;
import se.marej.atm.model.ATMCard;
import se.marej.atm.model.ATMReceipt;
import se.marej.atm.model.BankReceipt;

public final class ATMSessionImpl extends AbstractATMSession
{
	private boolean withdrawAmountCalled = false;
	private boolean checkBalanceCalled = false;

	public ATMSessionImpl(final ATMCard atmCard, final Bank bank)
	{
		super(atmCard, bank);
	}

	@Override
	public long withdrawAmount(final int amount)
	{
		if (withdrawAmountCalled)
		{
			throw new ATMException("Can't call same method twice in a row");
		}
		if (amount < 100 || amount > 10000 || amount % 100 != 0)
		{
			throw new ATMException("Not valid amount");
		}
		if (bank.getBalance(atmCard.getAccountHolderId()) >= amount)
		{
			withdrawAmountCalled = true;
			return bank.withdrawAmount(amount);
		}
		throw new ATMException("Not enough money");
	}

	@Override
	public ATMReceipt requestReceipt(final long transactionId)
	{
		BankReceipt reciept = bank.requestReceipt(transactionId);

		/**
		 * Eftersom kvittot skapas här kommer datumet på det vara för när man
		 * begärde kvittot, inte för när uttaget skedde.
		 */
		return new ATMReceipt(reciept.getTransactionId(), reciept.getAmount());
	}

	@Override
	public long checkBalance()
	{
		if (checkBalanceCalled)
		{
			throw new ATMException("Can't call same method twice in a row");
		}
		checkBalanceCalled = true;
		return bank.getBalance(atmCard.getAccountHolderId());
	}
}
