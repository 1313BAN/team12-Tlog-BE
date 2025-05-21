package com.ssafy.tlog.trip.record.service;

import com.ssafy.tlog.entity.AiStory;
import com.ssafy.tlog.entity.Trip;
import com.ssafy.tlog.entity.TripRecord;
import com.ssafy.tlog.exception.custom.InvalidUserException;
import com.ssafy.tlog.exception.custom.ResourceNotFoundException;
import com.ssafy.tlog.repository.AiStoryRepository;
import com.ssafy.tlog.repository.TripParticipantRepository;
import com.ssafy.tlog.repository.TripRecordRepository;
import com.ssafy.tlog.repository.TripRepository;
import com.ssafy.tlog.trip.record.dto.AiStoryResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordDetailResponseDto;
import com.ssafy.tlog.trip.record.dto.TripRecordDetailResponseDto.TripRecordDto;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiStoryService {

    private final TripRepository tripRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripRecordRepository tripRecordRepository;
    private final AiStoryRepository aiStoryRepository;
    private final ChatModel chatModel;
    private final RecordService recordService;

    @Transactional
    public AiStoryResponseDto generateAiStory(int userId, int tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("요청한 여행을 찾을 수 없습니다."));

        if (!tripParticipantRepository.existsByTripIdAndUserId(tripId, userId)) {
            throw new InvalidUserException("해당 여행 기록에 접근 권한이 없습니다.");
        }

        List<TripRecord> tripRecords = tripRecordRepository.findAllByTripIdAndUserIdOrderByDay(tripId, userId);
        if (tripRecords.isEmpty()) {
            throw new ResourceNotFoundException("AI 스토리를 생성하기 위한 충분한 여행 기록이 없습니다.");
        }

        TripRecordDetailResponseDto tripDetail = recordService.getTripRecordDetailByTripId(userId, tripId);
        String aiStoryContent = generateStoryContent(tripDetail);

        AiStory aiStory = aiStoryRepository.findByTripIdAndUserId(tripId, userId)
                .orElse(new AiStory());

        aiStory.setTripId(tripId);
        aiStory.setUserId(userId);
        aiStory.setContent(aiStoryContent);
        aiStoryRepository.save(aiStory);

        return AiStoryResponseDto.builder()
                .tripId(tripId)
                .aiStory(aiStoryContent)
                .build();
    }

    private String generateStoryContent(TripRecordDetailResponseDto tripDetail) {
        Map<String, Object> promptData = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

        promptData.put("title", tripDetail.getTrip().getTitle());
        promptData.put("startDate", tripDetail.getTrip().getStartDate().format(formatter));
        promptData.put("endDate", tripDetail.getTrip().getEndDate().format(formatter));
        promptData.put("tripDuration", tripDetail.getTripRecords().size());

        StringBuilder dayByDayContent = new StringBuilder();
        for (TripRecordDto record : tripDetail.getTripRecords()) {
            dayByDayContent.append("【 ")
                    .append(record.getDay())
                    .append("일차 】 ")
                    .append(record.getDate().format(formatter))
                    .append("\n")
                    .append(record.getMemo())
                    .append("\n\n");
        }
        promptData.put("dayByDayContent", dayByDayContent.toString());

        List<String> places = tripDetail.getTripPlans().stream()
                .flatMap(day -> day.getPlans().stream())
                .map(plan -> plan.getMemo())
                .filter(memo -> memo != null && !memo.isBlank())
                .collect(Collectors.toList());
        promptData.put("places", String.join(", ", places));

        String templateString = """
                당신은 여행 기록을 바탕으로 블로그 형식의 여행 정보를 작성하는 전문 작가입니다.
                다음 여행 기록을 바탕으로 생생하고 실용적인 여행 블로그 글을 작성해주세요.
                
                ## 여행 기본 정보
                - 여행 제목: {{title}}
                - 여행 기간: {{startDate}} ~ {{endDate}} (총 {{tripDuration}}일)
                
                ## 일자별 기록
                {{dayByDayContent}}
                
                ## 방문한 주요 장소
                {{places}}
                
                다음 지침을 따라주세요:
                1. 명확한 제목과 서브제목을 사용하여 구조화된 마크다운 형식으로 작성하세요.
                2. 1인칭 시점으로 작성하되, 마치 친구에게 여행 이야기를 들려주는 듯한 편안한 어투로 작성해주세요.
                3. 각 날짜별로 방문한 장소, 활동, 경험한 것을 구체적으로 나열하되, 단순 일정 나열이 아닌 여행 중 느낀 감정, 인상적인 장면, 음식, 만남 등을 생생하게 표현해주세요.
                4. 여행지의 분위기, 날씨, 풍경 등을 묘사하여 독자가 간접 체험할 수 있도록 해주세요.
                5. 방문한 장소의 특징이나 역사적 배경을 간략히 언급하되, 너무 교과서적인 설명은 피해주세요.
                6. 메모에 있는 내용을 충실히 반영하고, 없는 내용은 만들어내지 마세요.
                7. 장소별 특징, 추천 포인트, 방문 팁 등 실질적인 정보를 포함해주세요.
                8. 마크다운 문법(# 제목, ## 부제목, **굵게**, *기울임*, - 목록 등)을 올바르게 사용하고, 이스케이프 문자(\\n) 대신 실제 줄바꿈을 사용하세요.
                9. 실제 노션이나 다른 마크다운 편집기에 바로 복사-붙여넣기 했을 때 정상적으로 표시되도록 순수 마크다운 텍스트로 작성하세요.
                10. 각 날짜는 ## Day 1, ## Day 2 형식으로 명확하게 구분해주세요.
                11. 전체 글의 분량은 약 1000자 이내로 간결하되 생생하게 작성해주세요.
                12. 여행 계획에 없던 내용은 만들어내지 말고, 주어진 정보만을 기반으로 작성해주세요.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(templateString);
        Prompt prompt = promptTemplate.create(promptData);

        var result = chatModel.call(prompt);
        return result.getResult().getOutput().getText();
    }
}
