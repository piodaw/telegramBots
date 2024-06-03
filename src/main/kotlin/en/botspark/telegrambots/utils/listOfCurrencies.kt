package en.botspark.telegrambots.utils

val currencyList = listOf(
    "USD", // US Dollar
    "EUR", // Euro
    "GBP", // British Pound Sterling
    "JPY", // Japanese Yen
    "CAD", // Canadian Dollar
    "AUD", // Australian Dollar
    "CHF", // Swiss Franc
    "HKD", // Hong Kong Dollar
    "SGD", // Singapore Dollar
    "SEK", // Swedish Krona
    "DKK", // Danish Krone
    "NOK", // Norwegian Krone
    "NZD", // New Zealand Dollar
    "MXN", // Mexican Peso
    "BRL", // Brazilian Real
    "RUB", // Russian Ruble
    "INR", // Indian Rupee
    "ZAR", // South African Rand
    "TRY", // Turkish Lira
    "ARS"  // Argentine Peso
    // Add more currencies as needed
)

val subscriptionPeriod = listOf(
    0, 30, 365, 1, 3, 7, 90, 180
)

val mappedSubscriptionPeriod = subscriptionPeriod.map { it ->
    when (it) {
        0 -> "One time payment"
        30 -> "Every month"
        365 -> "Every year"
        1 -> "Every day"
        3 -> "Every 3 days"
        7 -> "Every week"
        90 -> "Every 3 months"
        180 -> "Every 6 months"
        else -> "Wrong subscription period"
    }
}