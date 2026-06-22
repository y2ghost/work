#include <iostream>
#include <sstream>
#include <string>

using namespace std;

class EmailBuilder;

class Email {
public:
    friend class EmailBuilder;
    static EmailBuilder make();

    string to_string() const {
        stringstream ss;
        ss << "from: " << from
           << "\nto: " << to
           << "\nsubject: " << subject
           << "\bbody: " << body;
        return ss.str();
    }

private:
    Email() = default;
    string from;
    string to;
    string subject;
    string body;
};

class EmailBuilder {
public:
    EmailBuilder& from(const string& from) {
        email.from = from;
        return *this;
    }

    EmailBuilder& to(const string& to) {
        email.to = to;
        return *this;
    }

    EmailBuilder& subject(const string& subject) {
        email.subject = subject;
        return *this;
    }

    EmailBuilder& body(const string& body) {
        email.body = body;
        return *this;
    }

    operator Email&&() {
        return std::move(email);
    }

private:
    Email email;
};

EmailBuilder Email::make()
{
    return EmailBuilder();
}

std::ostream& operator <<(std::ostream& out, const Email& email)
{
    out << email.to_string();
    return out;
}

int main(void)
{
    Email email = Email::make().from("test@example.org")
        .to("you@email.com")
        .subject("builder pattern")
        .body("just for learn");
    cout << email << endl;
    return 0;
}
