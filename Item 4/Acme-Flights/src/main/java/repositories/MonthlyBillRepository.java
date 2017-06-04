
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.MonthlyBill;

@Repository
public interface MonthlyBillRepository extends JpaRepository<MonthlyBill, Integer> {

	@Query("select m from MonthlyBill m where m.campaign.id=?1")
	Collection<MonthlyBill> findByCampaignId(int campaignId);

	@Query("select m from MonthlyBill m where m.airline.id=?1")
	Collection<MonthlyBill> findByAirlineId(int airlineId);

	@Query("select m from MonthlyBill m where m.paidMoment=null and m.campaign.id=?1")
	Collection<MonthlyBill> findUnpaidMonthlyBillsByCampaignId(int campaignId);

	@Query("select m from MonthlyBill m where m.airline.id=?1 and m.creationMoment >=ALL(select m1.creationMoment from MonthlyBill m1 where m1.airline.id=m.airline.id)")
	MonthlyBill findLastMonthlyBill(int airlineId);
}
