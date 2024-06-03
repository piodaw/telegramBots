package en.botspark.telegrambots.bots.stripeBot

import en.botspark.telegrambots.bots.BotConfig
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.ACCEPTED_TERMS
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.ACCEPT_TERMS_BUTTON
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.ACCEPT_TERMS_CALLBACK
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.ACCEPT_TERMS_MESSAGE
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.CHOOSE_PROJECT
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.CREATE_PROJECT
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.CREATE_PROJECT_CALLBACK
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.CREATE_PROJECT_MESSAGE
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.DONE
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.DONE_CALLBACK_CHANNEL
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.DONE_CALLBACK_GROUP
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.DONE_STEP3_CALLBACK_CHANNEL
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.DONE_STEP3_CALLBACK_GROUP
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.DONT_HAVE_PROJECT
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.GET_BOT_TOKEN
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.MANAGE_PROJECT
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.MANAGE_PROJECTS
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.MANAGE_PROJECTS_CALLBACK
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.MESSAGE_AFTER_CREATING_CHANNEL
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.MESSAGE_AFTER_CREATING_GROUP
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.PAID_TELEGRAM_CHANNEL
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.PAID_TELEGRAM_CHANNEL_CALLBACK
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.PAID_TELEGRAM_CHANNEL_MESSAGE
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.PAID_TELEGRAM_GROUP
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.PAID_TELEGRAM_GROUP_CALLBACK
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.PAID_TELEGRAM_GROUP_MESSAGE
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.PROJECTS_MENU_OPTIONS
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.STEP_4_MESSAGE
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.STEP_5_CURRENCY_MESSAGE
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.STEP_5_MORE_CURRENCY_CALLBACK
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.STEP_6_MORE_PERIODS
import en.botspark.telegrambots.bots.stripeBot.dict.PaymentBotDict.Companion.WELCOME_MESSAGE
import en.botspark.telegrambots.bots.stripeBot.enums.PaymentBotUserProjectTypes
import en.botspark.telegrambots.bots.stripeBot.services.PaymentBotService
import en.botspark.telegrambots.utils.currencyList
import en.botspark.telegrambots.utils.mappedSubscriptionPeriod
import org.springframework.stereotype.Component
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient

@Component
class PaymentBot(
    private val botConfig: BotConfig,
    private val paymentBotService: PaymentBotService
) : SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final val telegramClient: TelegramClient
    private var projectNameActive = false
    private var waitForForwardedMessage = false
    private var waitForPriceMsg = false
    private var waitForBotToken = false

    init {
        telegramClient = OkHttpTelegramClient(botToken)
    }

    override fun getBotToken(): String {
        return botConfig.paymentBotSecret
    }

    override fun getUpdatesConsumer(): LongPollingUpdateConsumer {
        return this
    }

    override fun consume(update: Update?) {
        if (update!!.hasMessage() && update.message.hasText()) {
            val messageText = update.message.text
            val chatId = update.message.chatId

            if (projectNameActive) {
                createNewProjectStep1(chatId, update.message.from.id, messageText)
            } else if (waitForForwardedMessage) {
                if (update.message.forwardFromChat != null) {
                    waitForForwardedMessage = false
                    createNewProjectStep5(chatId, update.message.from.id, update.message.forwardFromChat.id)
                } else {
                    val errorMessage = SendMessage
                        .builder()
                        .chatId(chatId)
                        .text("Please forward a message from the channel")
                        .build()
                    telegramClient.execute(errorMessage)
                }
            } else if (waitForPriceMsg) {
                if (messageText.matches(Regex("^[0-9]+(\\.[0-9]{1,2})?$"))) {
                    createNewProjectStep8(chatId, update.message.from.id, messageText.toLong())
                } else {
                    val errorMessage = SendMessage
                        .builder()
                        .chatId(chatId)
                        .text("Please enter a valid amount")
                        .build()
                    telegramClient.execute(errorMessage)
                }
            } else if (waitForBotToken) {
                if (checkIfBotTokenIsValidRegex(messageText)) {
                    afterAddingBotToken(chatId, messageText)
                } else {
                    val errorMessage = SendMessage
                        .builder()
                        .chatId(chatId)
                        .text("Bot token must have form \"12345678910:XXXXXXXXXXXX\".\n Please try again.")
                        .build()
                    telegramClient.execute(errorMessage)
                }
            } else {
                when (messageText) {
                    "/start" -> startMessage(chatId, update.message.from.id)
                    "My Bots" -> getMyBots(chatId)
                    else -> wrongCommand(chatId)
                }
            }
        } else if (update.hasCallbackQuery()) {
            val chatId = update.callbackQuery.message.chatId
            val data = update.callbackQuery.data

            when (data) {
                ACCEPT_TERMS_CALLBACK -> afterAcceptTerms(chatId, update.callbackQuery.from.id)
                CREATE_PROJECT_CALLBACK -> createNewProjectStep0(chatId, update.callbackQuery.from.id)
                PAID_TELEGRAM_CHANNEL_CALLBACK -> createNewProjectStep2(chatId, update.callbackQuery.from.id, PAID_TELEGRAM_CHANNEL_CALLBACK)
                PAID_TELEGRAM_GROUP_CALLBACK -> createNewProjectStep2(chatId, update.callbackQuery.from.id, PAID_TELEGRAM_GROUP_CALLBACK)
                DONE_CALLBACK_CHANNEL -> createNewProjectStep3(chatId, update.callbackQuery.from.id, DONE_CALLBACK_CHANNEL)
                DONE_CALLBACK_GROUP -> createNewProjectStep3(chatId, update.callbackQuery.from.id, DONE_CALLBACK_GROUP)
                DONE_STEP3_CALLBACK_CHANNEL -> createNewProjectStep4(chatId, update.callbackQuery.from.id)
                DONE_STEP3_CALLBACK_GROUP -> createNewProjectStep4(chatId, update.callbackQuery.from.id)
                in currencyList -> createNewProjectStep6(chatId, update.callbackQuery.from.id, data)
                STEP_5_MORE_CURRENCY_CALLBACK -> showMoreCurrencies(chatId, update.callbackQuery.from.id)
                STEP_6_MORE_PERIODS -> showMorePeriods(chatId, update.callbackQuery.from.id)
                in mappedSubscriptionPeriod -> createNewProjectStep7(chatId, update.callbackQuery.from.id, data)
                MANAGE_PROJECTS_CALLBACK -> manageProjects(chatId, update.callbackQuery.from.id)
                MANAGE_PROJECT + extractIdFromCallbackData(update.callbackQuery.data) -> manageProject(chatId, update.callbackQuery.from.id, extractIdFromCallbackData(update.callbackQuery.data))
                else -> wrongCommand(chatId)
            }
        }
    }

    fun startMessage(chatId: Long, userId: Long) {
        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text(WELCOME_MESSAGE)
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }

        startDetailsMessage(chatId, userId)
    }

    fun startDetailsMessage(chatId: Long, userId: Long) {
        paymentBotService.createUser(userId)

        if (!paymentBotService.checkIfUserAcceptedTerms(userId)) {
            val message = SendMessage
                .builder()
                .chatId(chatId)
                .text(ACCEPT_TERMS_MESSAGE)
                .replyMarkup(
                    InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                            InlineKeyboardRow(
                                InlineKeyboardButton
                                    .builder()
                                    .text(ACCEPT_TERMS_BUTTON)
                                    .callbackData(ACCEPT_TERMS_CALLBACK)
                                    .build()
                            )
                        )
                        .build())
                .build()
            try {
                telegramClient.execute(message)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        } else {
            createProject(chatId, userId)
        }
    }

    fun afterAcceptTerms(chatId: Long, userId: Long) {
        paymentBotService.updateUserAcceptedTerms(userId)

        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text(ACCEPTED_TERMS)
            .replyMarkup(
                ReplyKeyboardMarkup(
                    listOf(
                        KeyboardRow().apply {
                            add("My Bots")
                            add("Subscriptions")
                        }
                    )
                )
            )
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun createProject(chatId: Long, userId: Long) {
        if (paymentBotService.checkIfUserHasProjects(userId)) {
            val nextMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text(DONT_HAVE_PROJECT)
                .replyMarkup(
                    InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                            InlineKeyboardRow(
                                InlineKeyboardButton
                                    .builder()
                                    .text(CREATE_PROJECT)
                                    .callbackData(CREATE_PROJECT_CALLBACK)
                                    .build()
                            )
                        )
                        .build())
                .build()
            try {
                telegramClient.execute(nextMessage)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        } else {
            val message = SendMessage
                .builder()
                .chatId(chatId)
                .text(PROJECTS_MENU_OPTIONS)
                .replyMarkup(
                    InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                            InlineKeyboardRow(
                                InlineKeyboardButton
                                    .builder()
                                    .text(MANAGE_PROJECTS)
                                    .callbackData(MANAGE_PROJECTS_CALLBACK)
                                    .build()
                            )
                        )
                        .keyboardRow(
                            InlineKeyboardRow(
                                InlineKeyboardButton
                                    .builder()
                                    .text(CREATE_PROJECT)
                                    .callbackData(CREATE_PROJECT_CALLBACK)
                                    .build()
                            )
                        )
                        .build())
                .build()
            try {
                telegramClient.execute(message)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }

    fun createNewProjectStep0(chatId: Long, userId: Long) {
        projectNameActive = true
        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text("Please enter the name of the project")
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun createNewProjectStep1(chatId: Long, userId: Long, projectName: String) {
        projectNameActive = false
        paymentBotService.createNewProject(userId, projectName)

        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text(CREATE_PROJECT_MESSAGE.format(projectName))
            .replyMarkup(
                InlineKeyboardMarkup
                    .builder()
                    .keyboardRow(
                        InlineKeyboardRow(
                            InlineKeyboardButton
                                .builder()
                                .text(PAID_TELEGRAM_CHANNEL)
                                .callbackData(PAID_TELEGRAM_CHANNEL_CALLBACK)
                                .build()
                        )
                    )
                    .keyboardRow(
                        InlineKeyboardRow(
                            InlineKeyboardButton
                                .builder()
                                .text(PAID_TELEGRAM_GROUP)
                                .callbackData(PAID_TELEGRAM_GROUP_CALLBACK)
                                .build()
                        )
                    )
                    .build())
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun createNewProjectStep2(chatId: Long, userId: Long, projectType: String) {
        when (projectType) {
            PAID_TELEGRAM_CHANNEL_CALLBACK -> {
                paymentBotService.updateTelegramProjectType(userId, PaymentBotUserProjectTypes.CHANNEL)
                val message = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(PAID_TELEGRAM_CHANNEL_MESSAGE)
                    .replyMarkup(
                        InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                InlineKeyboardRow(
                                    InlineKeyboardButton
                                        .builder()
                                        .text(DONE)
                                        .callbackData(DONE_CALLBACK_CHANNEL)
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build()
                try {
                    telegramClient.execute(message)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
            }
            PAID_TELEGRAM_GROUP_CALLBACK -> {
                paymentBotService.updateTelegramProjectType(userId, PaymentBotUserProjectTypes.GROUP)
                val message = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(PAID_TELEGRAM_GROUP_MESSAGE)
                    .replyMarkup(
                        InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                InlineKeyboardRow(
                                    InlineKeyboardButton
                                        .builder()
                                        .text(DONE)
                                        .callbackData(DONE_CALLBACK_GROUP)
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build()
                try {
                    telegramClient.execute(message)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
            }
            else -> {}
        }
    }

    fun createNewProjectStep3(chatId: Long, userId: Long, projectType: String) {
        when (projectType) {
            DONE_CALLBACK_CHANNEL -> {
                val message = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(MESSAGE_AFTER_CREATING_CHANNEL)
                    .replyMarkup(
                        InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                InlineKeyboardRow(
                                    InlineKeyboardButton
                                        .builder()
                                        .text(DONE)
                                        .callbackData(DONE_STEP3_CALLBACK_CHANNEL)
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build()
                try {
                    telegramClient.execute(message)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
            }
            DONE_CALLBACK_GROUP -> {
                val message = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(MESSAGE_AFTER_CREATING_GROUP)
                    .replyMarkup(
                        InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                InlineKeyboardRow(
                                    InlineKeyboardButton
                                        .builder()
                                        .text(DONE)
                                        .callbackData(DONE_STEP3_CALLBACK_GROUP)
                                        .build()
                                )
                            )
                            .build()
                    )
                    .build()
                try {
                    telegramClient.execute(message)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                }
            }
            else -> {}
        }
    }

    fun createNewProjectStep4(chatId: Long, userId: Long) {
        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text(STEP_4_MESSAGE)
            .build()
        try {
            telegramClient.execute(message)
            waitForForwardedMessage = true
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun createNewProjectStep5(chatId: Long, userId: Long, channelId: Long) {
        paymentBotService.updateTelegramProjectChannelId(userId, channelId)
        val mainCurrencies = currencyList.take(3) + "More"
        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text(STEP_5_CURRENCY_MESSAGE)
            .replyMarkup(
                InlineKeyboardMarkup(
                    mainCurrencies.map { it ->
                        InlineKeyboardRow().apply {
                            add(
                                it.let {
                                    InlineKeyboardButton
                                        .builder()
                                        .text(it)
                                        .callbackData(it)
                                        .build()
                                }
                            )
                        }
                    }
                )
            )
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun createNewProjectStep6(chatId: Long, userId: Long, currency: String) {
        paymentBotService.updateTelegramProjectCurrency(userId, currency)

        val mainSubscriptionPeriods = mappedSubscriptionPeriod.take(3) + "More"

        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text("How often would you like your members to pay?")
            .replyMarkup(
                InlineKeyboardMarkup(
                    mainSubscriptionPeriods.map { it ->
                        InlineKeyboardRow().apply {
                            add(
                                it.let {
                                    InlineKeyboardButton
                                        .builder()
                                        .text(it)
                                        .callbackData(it)
                                        .build()
                                }
                            )
                        }
                    }
                )
            )
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun createNewProjectStep7(chatId: Long, userId: Long, period: String) {
        paymentBotService.updateTelegramProjectPeriod(userId, mapTextToDigitPeriod(period))

        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text("How much will they pay (every month, in USD)?")
            .build()
        try {
            waitForPriceMsg = true
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun createNewProjectStep8(chatId: Long, userId: Long, price: Long) {
        waitForPriceMsg = false
        paymentBotService.updateTelegramProjectPrice(userId, price)
        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text("Please open a chat with @BotFather and create a new bot. Copy and paste the bot token here: ")
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun afterAddingBotToken(chatId: Long, botToken: String) {
        paymentBotService.updateTelegramProjectBotToken(chatId, botToken)
//        @TODO
        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text("Bot token added successfully")
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun showMoreCurrencies(chatId: Long, userId: Long) {
        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text("More currencies")
            .replyMarkup(
                InlineKeyboardMarkup(
                    currencyList.drop(3).map { it ->
                        InlineKeyboardRow().apply {
                            add(
                                it.let {
                                    InlineKeyboardButton
                                        .builder()
                                        .text(it)
                                        .callbackData(it)
                                        .build()
                                }
                            )
                        }
                    }
                )
            )
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun showMorePeriods(chatId: Long, userId: Long) {
        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text("More subscription periods")
            .replyMarkup(
                InlineKeyboardMarkup(
                    mappedSubscriptionPeriod.drop(3).map { it ->
                        InlineKeyboardRow().apply {
                            add(
                                it.let {
                                    InlineKeyboardButton
                                        .builder()
                                        .text(it)
                                        .callbackData(it + "_period")
                                        .build()
                                }
                            )
                        }
                    }
                )
            )
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun manageProjects(chatId: Long, userId: Long) {
        val userProjects = paymentBotService.getUserProjects(userId)

        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text(CHOOSE_PROJECT)
            .replyMarkup(
                InlineKeyboardMarkup(
                    userProjects.map { project ->
                        InlineKeyboardRow().apply {
                            add(
                                project.name?.let {
                                    InlineKeyboardButton
                                        .builder()
                                        .text(it)
                                        .callbackData(MANAGE_PROJECT + project.id)
                                        .build()
                                }
                            )
                        }
                    }
                )
            )
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun manageProject(chatId: Long, userId: Long, projectId: Long) {
        val project = paymentBotService.getProject(projectId)

//        @TODO

        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text("Project name: ${project.name}")
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun getMyBots(chatId: Long) {
//        @TODO
        val botInfo = paymentBotService.getBotInfoFromTelegramApi()

        if (botInfo != null) {
            val message = SendMessage
                .builder()
                .chatId(chatId)
                .text("Bot name: @${botInfo.result.username}")
                .build()
            try {
                telegramClient.execute(message)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        } else {
            val message = SendMessage
                .builder()
                .chatId(chatId)
                .text("You have no bots")
                .build()
            try {
                telegramClient.execute(message)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }

    fun getBotToken(chatId: Long) {
//        @TODO
        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text(GET_BOT_TOKEN)
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    fun wrongCommand(chatId: Long) {
        val message = SendMessage
            .builder()
            .chatId(chatId)
            .text("Wrong command")
            .build()
        try {
            telegramClient.execute(message)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }

    }

    fun extractIdFromCallbackData(data: String): Long {
        return data.split("_")[2].toLong()
    }

    fun checkIfBotTokenIsValidRegex(token: String): Boolean {
        return token.matches(Regex("^[0-9]{9}:[a-zA-Z0-9_-]{35}$"))
    }

    fun mapTextToDigitPeriod(subscriptionText: String): Int {
        return when (subscriptionText) {
            "One time payment" -> 0
            "Every month" -> 30
            "Every year" -> 365
            "Every day" -> 1
            "Every 3 days" -> 3
            "Every week" -> 7
            "Every 3 months" -> 90
            "Every 6 months" -> 180
            else -> 0
        }
    }
}