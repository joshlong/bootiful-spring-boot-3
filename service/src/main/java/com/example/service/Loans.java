package com.example.service;

class Loans {

	sealed interface Loan permits SecuredLoan, UnsecuredLoan {

	}

	record UnsecuredLoan(float interest) implements Loan {
	}

	final class SecuredLoan implements Loan {

	}

	String displayMessageFor(Loan loan) {
		return switch (loan) {
			case SecuredLoan sl -> "good job! ";
			case UnsecuredLoan(var interest) -> "ouch! that " + interest + "% interest rate is going to hurt!";
		};
	}

}
