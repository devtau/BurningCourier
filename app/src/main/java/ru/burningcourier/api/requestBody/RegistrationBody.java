
package ru.burningcourier.api.requestBody;

public class RegistrationBody {

    private String phone;
    private String password;
    private int code;//SMS
    private String name;
    private String birth_day;
    private String sex;//m/f
    private String email;
    private String provider;//Идентификатор соц. сети (vk, fb, tw, ok), при регистрации через соц. сеть.
    private String social_id;//Идентификатор пользователя в соц.сети


    public RegistrationBody(String phone, String password, int smsValidationCode, String name,
                            String birthday, String sex, String email, String socialProvider, String socialId) {
        this.phone = phone;
        this.password = password;
        this.code = smsValidationCode;
        this.name = name;
        this.birth_day = birthday;
        this.sex = sex;
        this.email = email;
        this.provider = socialProvider;
        this.social_id = socialId;
    }
}
