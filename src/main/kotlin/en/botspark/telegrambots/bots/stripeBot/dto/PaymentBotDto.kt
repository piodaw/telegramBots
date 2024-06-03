package en.botspark.telegrambots.bots.stripeBot.dto

data class PaymentBotResponseDto(
    val ok: Boolean,
    val result: PaymentBotResultDto
)

data class PaymentBotResultDto(
    val id: Long,
    val is_bot: Boolean,
    val first_name: String,
    val username: String,
    val can_join_groups: Boolean,
    val can_read_all_group_messages: Boolean,
    val supports_inline_queries: Boolean,
    val can_connect_to_business: Boolean
)
