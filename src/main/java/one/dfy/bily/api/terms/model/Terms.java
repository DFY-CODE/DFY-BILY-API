package one.dfy.bily.api.terms.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import one.dfy.bily.api.terms.constant.TermsCode;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TBL_TERMS")
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TermsCode code;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean required;

    @Column(name = "content_url")
    private String contentUrl;


    public Terms(TermsCode code, String title, boolean required, String contentUrl) {
        this.code = code;
        this.title = title;
        this.required = required;
        this.contentUrl = contentUrl;
    }
}
