package com.frogermcs.gactions;

import com.frogermcs.gactions.api.SupportedPermissions;
import com.frogermcs.gactions.api.response.RootResponse;
import com.frogermcs.gactions.api.response.SpeechResponse;
import com.frogermcs.gactions.api.response.SuggestionResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by froger_mcs on 28/04/2017.
 */
public class ResponseBuilderTest {

    @Before
    public void setUp() throws Exception {

    }


    @Test
    public void testShouldBuildTellResponseWithRichContentAndSsml() throws Exception {
        String expectedSsml = "<speak>Lorem <break time='200ms'/> ipsum</speak>";
        String expectedDislpayText = "Lorem ipsum";

        SpeechResponse speechResponse = SpeechResponse.builder().ssml(expectedSsml).displayText(expectedDislpayText).build();
        RootResponse rootResponse = ResponseBuilder.tellResponseWithRichInput(speechResponse);

        assertEquals(false, rootResponse.expectUserResponse);
        assertNotNull("richResponse can not be null", rootResponse.finalResponse.richResponse);
        assertNotNull("richResponse must contain an array of items not null", rootResponse.finalResponse.richResponse.items);
        assertTrue("richResponse must contain an array of items of size equals 1", rootResponse.finalResponse.richResponse.items.size() == 1);
        assertEquals(expectedDislpayText, rootResponse.finalResponse.richResponse.items.get(0).simpleResponse.displayText);
        assertEquals(expectedSsml, rootResponse.finalResponse.richResponse.items.get(0).simpleResponse.ssml);
    }


    @Test
    public void testShouldBuildTellResponseWithRichContentAndTextToSpeech() throws Exception {
        String expectedTextToSpeech = "Lorem ipsum text to speech";
        String expectedDislpayText = "Lorem ipsum display text";

        SpeechResponse speechResponse = SpeechResponse.builder().textToSpeech(expectedTextToSpeech).displayText(expectedDislpayText).build();
        RootResponse rootResponse = ResponseBuilder.tellResponseWithRichInput(speechResponse);

        assertEquals(false, rootResponse.expectUserResponse);
        assertNotNull("richResponse can not be null", rootResponse.finalResponse.richResponse);
        assertNotNull("richResponse must contain an array of items not null", rootResponse.finalResponse.richResponse.items);
        assertTrue("richResponse must contain an array of items of size equals 1", rootResponse.finalResponse.richResponse.items.size() == 1);
        assertEquals(expectedDislpayText, rootResponse.finalResponse.richResponse.items.get(0).simpleResponse.displayText);
        assertEquals(expectedTextToSpeech, rootResponse.finalResponse.richResponse.items.get(0).simpleResponse.textToSpeech);
    }

    @Test
    public void testShouldBuildAskResponseWithRichInput() throws Exception {
        String expectedSsml = "<speak>Lorem <break time='200ms'/> ipsum</speak>";
        String expectedDislpayText = "Lorem ipsum";

        SpeechResponse speechResponse = SpeechResponse.builder().ssml(expectedSsml).displayText(expectedDislpayText).build();
        RootResponse rootResponse = ResponseBuilder.askResponseWithRichInput(speechResponse);

        assertEquals(true, rootResponse.expectUserResponse);
        assertEquals(expectedSsml, rootResponse.expectedInputs.get(0).inputPrompt.richInitialPrompt.items.get(0).simpleResponse.ssml);
        assertEquals(expectedDislpayText, rootResponse.expectedInputs.get(0).inputPrompt.richInitialPrompt.items.get(0).simpleResponse.displayText);
    }


    @Test
    public void testShouldBuildAskResponseWithRichInput_and_options() throws Exception {
        String expectedSsml = "<speak>Lorem <break time='200ms'/> ipsum</speak>";
        String expectedDislpayText = "Lorem ipsum";

        String expectedNoInputPromptSsml = "<speak>Lorem <break time='200ms'/> ipsum</speak>";
        String expectedNoInputPromptDislpayText = "Lorem ipsum";

        String conversationToke = "a conversation token";

        SpeechResponse speechResponse = SpeechResponse.builder().ssml(expectedSsml).displayText(expectedDislpayText).build();
        SpeechResponse noInputPrompt = SpeechResponse.builder().ssml(expectedNoInputPromptSsml).displayText(expectedNoInputPromptDislpayText).build();

        List<SuggestionResponse> suggestions = new ArrayList<>();
        suggestions.add(SuggestionResponse.builder().title("suggestion 1").build());
        suggestions.add(SuggestionResponse.builder().title("suggestion 2").build());

        RootResponse rootResponse = ResponseBuilder.askResponseWithRichInput(speechResponse, conversationToke, Collections.singletonList(noInputPrompt), suggestions);

        assertEquals(true, rootResponse.expectUserResponse);

        assertEquals(expectedSsml, rootResponse.expectedInputs.get(0).inputPrompt.richInitialPrompt.items.get(0).simpleResponse.ssml);
        assertEquals(expectedDislpayText, rootResponse.expectedInputs.get(0).inputPrompt.richInitialPrompt.items.get(0).simpleResponse.displayText);

        assertNotNull(rootResponse.expectedInputs.get(0).inputPrompt.noInputPrompts);
        assertTrue("noInputPrompt must be of size 1", rootResponse.expectedInputs.get(0).inputPrompt.noInputPrompts.size() == 1);

        assertNotNull(expectedSsml, rootResponse.expectedInputs.get(0).inputPrompt.richInitialPrompt.suggestions);
        assertTrue("Suggestions must be of size " + suggestions.size(), rootResponse.expectedInputs.get(0).inputPrompt.richInitialPrompt.suggestions.size() == suggestions.size());
    }

    @Test
    public void testShouldBuildTellResponse() throws Exception {
        String expectedSpeech = "expected_speech";
        RootResponse rootResponse = ResponseBuilder.tellResponse(expectedSpeech);
        assertEquals(expectedSpeech, rootResponse.finalResponse.speechResponse.textToSpeech);
    }

    @Test
    public void testShouldBuildAskResponse() throws Exception {
        String expectedPrompt = "expected_prompt";
        RootResponse rootResponse = ResponseBuilder.askResponse(expectedPrompt);
        assertEquals(true, rootResponse.expectUserResponse);
        assertEquals(expectedPrompt, rootResponse.expectedInputs.get(0).inputPrompt.initialPrompts.get(0).textToSpeech);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPermission_whenPermissionContextIsEmpty_thenShouldThrowException() throws Exception {
        ResponseBuilder.askForPermissionResponse(null, new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPermission_whenPermissionsAreEmpty_thenShouldThrowException() throws Exception {
        ResponseBuilder.askForPermissionResponse("Lorem ipsum", new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPermission_whenAnyPermissionIsIncorrect_thenShouldThrowException() throws Exception {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(SupportedPermissions.DEVICE_COARSE_LOCATION.name());
        permissions.add("incorrect permission");
        ResponseBuilder.askForPermissionResponse("Lorem ipsum", permissions);
    }
}