package com.example.service;

sealed interface Loan permits SecuredLoan, UnsecuredLoan {
}

record UnsecuredLoan(float interest) implements Loan {
}

final class SecuredLoan implements Loan {
}
