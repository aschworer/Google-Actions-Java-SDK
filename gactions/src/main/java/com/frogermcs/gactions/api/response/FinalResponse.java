package com.frogermcs.gactions.api.response;

import lombok.*;

@Builder
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FinalResponse {
    public RichInitialPrompt richResponse;
    public SpeechResponse speechResponse;
}
