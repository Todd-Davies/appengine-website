var compute = function(oneoff, loan, plan, income, p1interest) {
  const plan1 = plan == "p1"
  var interest = 0;
  if (plan1) {
    interest = p1interest;
  } else {
    const rpi = 3.1;
    const added = income <= 21000 ? 0 : (41000.0 / Math.min(income, 41000)) * 3.0;
    interest = rpi + added;
  }

  // Only charged after 21k
  const non_taxable = plan1 ? 17500 : 21000;
  // The percentage of salary that is deducted
  const tax_amount = 9.0;

  var total_contribution = oneoff;
  var total_interest = 0;
  var year = 2018; // TODO: update this

  loan -= oneoff;

  const monthly_payment = Math.floor((((income - non_taxable) / 100.0) * tax_amount) / 12.0);
  while (loan > 0 && year <= (2018 + 30)) {
    // Simulate every month
    for (var i = 0; i < 12; i++) {
      const interest_amount = (loan / 100.0 * interest) / 12.0;

      loan += interest_amount;
      if (loan <= monthly_payment) {
	total_contribution += loan;
	total_interest += interest_amount;
	loan = 0;
	break;
      } else {
	total_contribution += monthly_payment;
	total_interest += interest_amount;
	loan -= monthly_payment;
      }
    }

    year++;
  }

  return [total_interest, year, total_contribution, loan].map(function(i) { return parseInt(i); });
}

var refreshData = function(loan, plan, p1interest, income) {
  var data = []
  for (var oneoff = 0; oneoff <= loan; oneoff += 500) {
    const x = compute(oneoff,  loan, plan, income, p1interest);
    data.push([oneoff, x[0], x[1], x[2], x[3]]);
  }
  return data;
}
