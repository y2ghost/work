from collections import Counter, defaultdict
import data.game as data

HIT = "H"
MISS = "M"
CLOSE = "C"
ERROR = ""


def get_word() -> str:
    return data.get_word()


def get_score(actual: str, guess: str) -> str:
    length: int = len(actual)
    if len(guess) != length:
        return ERROR
    actual_counter = Counter(actual)
    guess_counter = defaultdict(int)
    result = [MISS] * length
    for pos, letter in enumerate(guess):
        if letter == actual[pos]:
            result[pos] = HIT
            guess_counter[letter] += 1
    for pos, letter in enumerate(guess):
        if result[pos] == HIT:
            continue
        guess_counter[letter] += 1
        if letter in actual and guess_counter[letter] <= actual_counter[letter]:
            result[pos] = CLOSE
    result = "".join(result)
    return result
