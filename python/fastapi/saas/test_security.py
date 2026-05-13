from security import authenticate_user


def test_authenticate_user_with_username(
    fill_database_session,
):
    user = authenticate_user(
        fill_database_session, "test", "pass1234"
    )
    assert user.username == "test"
    assert user.email == "test@email.com"


def test_authenticate_user_with_email(
    fill_database_session,
):
    user = authenticate_user(
        fill_database_session,
        "test@email.com",
        "pass1234",
    )
    assert user.username == "test"
    assert user.email == "test@email.com"


def test_authenticate_user_dot_find_username(session):
    assert (
        authenticate_user(
            session, "non_existing_user", "pass1234"
        )
        is None
    )


def test_authenticate_user_incorrect_password(session):
    assert (
        authenticate_user(
            session,
            "test@email.com",
            "incorrect_password",
        )
        is None
    )

