package ru.burningcourier.api;

abstract class ErrorConstants {
    
	static final int ANAUTHORIZED = 401;
	static final int TOKEN_CREATION_FAILURE = 104;
	static final int TOKEN_EXPIRED = 105;
	
	static final int INTERNAL_SERVER_ERROR = 500;
	static final int NO_SUCH_USER = 101;
	static final int WRONG_PASSWORD = 102;
	static final int ALREADY_LOGGED_IN = 103;
}
