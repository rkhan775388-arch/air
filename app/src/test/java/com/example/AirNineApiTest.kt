package com.example

import com.example.data.api.AirNineApiException
import com.example.data.api.GeminiApiClient
import com.example.data.model.MessageRole
import com.example.data.repository.AirNineRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AirNineApiTest {

    @Test
    fun testAvailableModelsAreModernAndNonEmpty() {
        val repository = AirNineRepository()
        assertTrue(repository.availableModels.isNotEmpty())
        val defaultModel = repository.selectedModel.value
        assertEquals("gemini-3.5-flash", defaultModel.id)
        assertTrue(repository.availableModels.any { it.id == "gemini-3.5-flash" })
        assertTrue(repository.availableModels.any { it.id == "gemini-3.1-pro-preview" })
    }

    @Test
    fun testSendMessageAddsUserAndAssistantMessages() = runBlocking {
        val repository = AirNineRepository()
        val initialCount = repository.messages.value.size

        val result = repository.sendMessage("Hello Air Nine test")
        val updatedMessages = repository.messages.value

        assertEquals(initialCount + 2, updatedMessages.size)
        assertEquals(MessageRole.USER, updatedMessages[initialCount].role)
        assertEquals("Hello Air Nine test", updatedMessages[initialCount].content)
        assertEquals(MessageRole.ASSISTANT, updatedMessages[initialCount + 1].role)

        assertFalse(repository.isGenerating.value)
    }

    @Test
    fun testCancelGenerationResetsGeneratingState() {
        val repository = AirNineRepository()
        repository.cancelGeneration()
        assertFalse(repository.isGenerating.value)
    }

    @Test
    fun testExceptionHierarchyUserFriendlyMessages() {
        val missingKey = AirNineApiException.MissingApiKeyException()
        assertTrue(missingKey.userFriendlyMessage.contains("Secrets panel"))

        val invalidKey = AirNineApiException.InvalidCredentialsException()
        assertTrue(invalidKey.userFriendlyMessage.contains("Invalid or unauthorized"))

        val quota = AirNineApiException.QuotaExceededException()
        assertTrue(quota.userFriendlyMessage.contains("quota exceeded"))

        val network = AirNineApiException.NetworkUnavailableException()
        assertTrue(network.userFriendlyMessage.contains("Network connection unavailable"))
    }

    @Test
    fun testRealGeminiApiGeneratesContent() = runBlocking {
        val client = GeminiApiClient()
        if (client.isApiKeyConfigured()) {
            val result = client.generateContent("Reply with the single word: CONFIRMED")
            assertTrue("API call should succeed with configured key", result.isSuccess)
            val text = result.getOrNull()
            assertNotNull(text)
            assertTrue("Response must contain text", text!!.isNotBlank())
        }
    }
}
