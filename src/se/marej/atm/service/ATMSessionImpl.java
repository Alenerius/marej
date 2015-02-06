package se.marej.atm.service;

import se.marej.atm.excaption.ATMException;
import se.marej.atm.model.ATMCard;
import se.marej.atm.model.ATMReceipt;

public class ATMSessionImpl extends AbstractATMSession
{
	private int amount;
	private boolean withdrawAmountCalled = false;
	private boolean checkBalanceCalled = false;

	public ATMSessionImpl(ATMCard atmCard, Bank bank)
	{
		super(atmCard, bank);
	}

	@Override
	public long withdrawAmount(int amount)
	{
		if (withdrawAmountCalled)
		{
			throw new ATMException("Can't call same method twice in a row");
		}
		withdrawAmountCalled = true;
		if (amount < 100 || amount > 10000 || amount % 100 != 0)
		{
			throw new ATMException("Not valid amount");
		}
		if (bank.getBalance(atmCard.getAccountHolderId()) >= amount)
		{
			return bank.withdrawAmount(amount);
		}
		throw new ATMException("Not enough money");
	}

	@Override
	public ATMReceipt requestReceipt(long transactionId)
	{
		return new ATMReceipt(transactionId, amount);
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
