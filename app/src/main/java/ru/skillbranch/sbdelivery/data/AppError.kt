package ru.skillbranch.sbdelivery.data

enum class AppError {
    E_SUCCESS,
    E_NOT_MODIFIED,
    E_UNKNOWN,
    E_NOT_CONNECTED,
    E_TIMEOUT_SOCKET,
    E_LOGIN_FAILED,
    E_LOGIN_EXISTS,
    E_LOGIN_ERROR,
    E_REG_FAILED,
    E_REG_EXISTS,
    E_RECOVERY_EMAIL_FAILED,
    E_RECOVERY_EMAIL_LESS_TIME,
    E_RECOVERY_CODE_FAILED,
    E_RECOVERY_CODE_WRONG,
    E_RECOVERY_PASSWORD_FAILED,
    E_RECOVERY_PASSWORD_EXPIRED,
    E_REFRESH_TOKEN_FAILED,
    E_REFRESH_TOKEN_EXPIRED,
    E_PROFILE_GET_FAILED,
    E_PASSWORD_CHANGE_FAILED,
    E_FAVORITE_FAILED,
    E_GET_RECOMMENDED_FAILED,
    E_GET_CATEGORIES_FAILED,
    E_GET_DISHES_FAILED,
    E_GET_REVIEWS_FAILED,
    E_ADD_REVIEWS_FAILED,
    E_GET_OR_UPDATE_CART_FAILED,
    E_CHECK_ADDRESS_FAILED,
    E_ORDER_NEW_FAILED,
    E_GET_ORDERS_FAILED,
    E_GET_ORDERS_STATUSES_FAILED,
    E_ORDER_CANCEL_FAILED,
    E_NOT_FOUND;
}
