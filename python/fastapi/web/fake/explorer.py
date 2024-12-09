from model.explorer import Explorer
from error import Missing, Duplicate

fakes = [
    Explorer(name="Claude Hande", country="FR", description="Scarce during full moons"),
    Explorer(name="Noah Weiser", country="DE", description="Myopic machete man"),
]


def find(name: str) -> Explorer | None:
    for e in fakes:
        if e.name == name:
            return e
    return None


def check_missing(name: str):
    if not find(name):
        raise Missing(msg=f"Missing explorer {name}")


def check_duplicate(name: str):
    if find(name):
        raise Duplicate(msg=f"Duplicate explorer {name}")


def get_all() -> list[Explorer]:
    return fakes


def get_one(name: str) -> Explorer:
    check_missing(name)
    return find(name)


def create(explorer: Explorer) -> Explorer:
    check_duplicate(explorer.name)
    return explorer


def modify(name: str, explorer: Explorer) -> Explorer:
    check_missing(name)
    return explorer


def delete(name: str) -> None:
    check_missing(name)
    return None
