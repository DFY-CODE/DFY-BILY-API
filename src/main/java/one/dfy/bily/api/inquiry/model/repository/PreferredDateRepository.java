package one.dfy.bily.api.inquiry.model.repository;

import one.dfy.bily.api.inquiry.model.Inquiry;
import one.dfy.bily.api.inquiry.model.PreferredDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferredDateRepository extends JpaRepository<PreferredDate, Long> {
    List<PreferredDate> findByInquiry(Inquiry inquiry);
}
