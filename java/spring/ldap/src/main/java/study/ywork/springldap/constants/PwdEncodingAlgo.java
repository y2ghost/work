package study.ywork.springldap.constants;

public enum PwdEncodingAlgo {
    BCRYPT("bcrypt"), PBKDF2("pbkdf2"), SCRYPT("scrypt");

    private String status;

    private PwdEncodingAlgo(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
