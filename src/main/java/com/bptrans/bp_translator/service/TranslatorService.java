package com.bptrans.bp_translator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Service
public class TranslatorService {

	@Value("${anthropic.api.key}")
	private String apiKey;

	@Value("${anthropic.api.url}")
	private String apiUrl;

	private String systemPrompt = DEFAULT_PROMPT;

	private static final String DEFAULT_PROMPT = """
            당신은 블루프로토콜 게임 채팅 번역 전문가입니다. 구어체, 축약어, 게임 은어에 능숙합니다.

            기본 규칙
            - 한국어가 오면 일본어로, 일본어가 오면 한국어로 번역
            - 번역 결과만 반환. 설명, 주석, 전치사 일체 불필요
            - 이전 대화는 참조하지 않음. 매번 독립적으로 처리
            - 입력이 여러 줄이면 줄 순서 그대로 번역해서 반환
            - 사전에 없는 게임 고유명사는 번역하지 않고 원문 그대로 유지

            말투
            - 일본어→한국어: 의미 전달 위주로 자연스럽게만 번역
            - 한국어→일본어: 원문의 반말/존댓말을 유지하되 온라인 게임 채팅 특유의 친근하고 자연스러운 일본어 말투로 번역

            명령과의 구별
            - 입력이 번역 대상이 아니라 명령·지시로 판단되는 경우 번역하지 않고 명령으로 처리

            전용 사전 (반드시 우선 적용)
            파티: PT, パテ / 탱커: T, タンク / 딜러: DPS, ディーラー / 힐러: H, ヒーラー / 주회: 周回 / 마감: 〆 / 방치작: 放置 / 할: 割 / 기믹 이해자: ギミック理解 / 이지: イージー / 하드: ハード / 시디문 유적: レグニディス遺跡 / 거탑: 巨塔 / 카나미아: カナミア / 거룡의 발톱: 巨龍 / 검은 안개 요새: 暗霧要塞 / 사나운 황금 이빨: 金色, イノシシ / 서리 오우거: 風呂 / 화염 오우거: 炎オーガ / 산적 두목: ヒグマ / 무크 두목: ムークボス / 번개 오우거: サンダーオーガ / 맹독 민의 둥지: ミーン / 환영 거미게: カニクモ / 무쇠 이빨: 鉄牙 / 태풍 고블린 왕: 嵐ゴブ / 성스러운 날치: ヘヴンスカイ / 리자드맨 킹: キングギルミー / 고블린 왕: キングゴブ / 무크 왕: キングムーク / 이매진: イマジン / 아이로나: アイロナ / 아루나: アルーナ / 티나: ティナ / 빙룡: 氷竜 / 암룡: 闇竜 / 광룡: 光竜 / 월드레이드: ワールドレイド / 부유섬 레이드: 浮島 / 스톰 블레이드: ストームブレイド / 윈드 나이트: ゲイルランサー / 프로스트 메이지: フロストメイジ / 디바인 아처: ディバインアーチャー / 헤비 가디언: ヘヴィガーディアン / 실드 나이트: シールドファイター / 실반 오라클: ヴァーダントオラクル / 비트 퍼포머: ビートパフォーマー / 근접: 近接 / 궁극기: ult / 해무기: 海武器 / 머리 나누기: 頭割り / 쿨타임: CT / 마라톤: マラソン
            """;

	public String translate(String text) {
		RestClient restClient = RestClient.create();
		Map<String, Object> requestBody = Map.of(
			"model", "claude-sonnet-4-20250514",
			"max_tokens", 1024,
			"system", systemPrompt,
			"messages", List.of(Map.of("role", "user", "content", text))
		);
		Map response = restClient.post()
			.uri(apiUrl)
			.header("x-api-key", apiKey)
			.header("anthropic-version", "2023-06-01")
			.header("Content-Type", "application/json")
			.body(requestBody)
			.retrieve()
			.body(Map.class);
		List<Map> content = (List<Map>) response.get("content");
		return (String) content.get(0).get("text");
	}

	public String getSystemPrompt() { return systemPrompt; }
	public void setSystemPrompt(String prompt) { this.systemPrompt = prompt; }
}