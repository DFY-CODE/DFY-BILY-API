package one.dfy.bily.api.terms.service;

import lombok.RequiredArgsConstructor;
import one.dfy.bily.api.terms.constant.TermsCode;
import one.dfy.bily.api.terms.model.Terms;
import one.dfy.bily.api.terms.model.UserTermsAgreement;
import one.dfy.bily.api.terms.model.repository.TermsRepository;
import one.dfy.bily.api.terms.model.repository.UserTermsAgreementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsService {
    private final TermsRepository termsRepository;
    private final UserTermsAgreementRepository userTermsAgreementRepository;

    public void createUserTermAgreement(List<TermsCode> termsCodeList, Long userId) {
        List<Terms> termsList = termsRepository.findByCodeIn(termsCodeList);

        List<UserTermsAgreement> agreements = termsList.stream()
                .map(terms -> new UserTermsAgreement(userId, terms))
                .toList();

        userTermsAgreementRepository.saveAll(agreements);
    }
}
