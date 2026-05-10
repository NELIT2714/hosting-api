package dev.nelit.api.domain.entity.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "users_telegram")
public class UserTelegram {

    @Id
    @Column("id_user_telegram")
    private Long idUserTelegram;

    @Column("id_user")
    private Long idUser;

    @Column("telegram_id")
    private String telegramId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("username")
    private String username;

    @Column("language_code")
    private String languageCode;

}
