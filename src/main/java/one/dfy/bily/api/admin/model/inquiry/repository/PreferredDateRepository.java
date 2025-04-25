package one.dfy.bily.api.admin.model.inquiry.repository;

import one.dfy.bily.api.admin.model.inquiry.Inquiry;
import one.dfy.bily.api.admin.model.inquiry.PreferredDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferredDateRepository extends JpaRepository<PreferredDate, Long> {
    List<PreferredDate> findByInquiry(Inquiry inquiry);
}
