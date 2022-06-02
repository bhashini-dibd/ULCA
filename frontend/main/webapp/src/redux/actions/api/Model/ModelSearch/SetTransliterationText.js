import C from "../../../constants";

export const setCurrentText = (text) => {
  return {
    type: C.SET_CURRENT_TEXT,
    payload: text,
  };
};

export const setTransliterationText = (prevText, newWord) => {
  return {
    type: C.SET_TRANSLITERATION_TEXT,
    payload: { prevText, newWord },
  };
};

export const clearTransliterationResult = () => {
  return {
    type: C.CLEAR_TRANSLITERATION_RESULT,
  };
};
