package models.org.ludwiggj.finance.domain

case class CashDelta(val amountIn: BigDecimal, val total: BigDecimal) {
  def add(delta: CashDelta) = CashDelta(amountIn + delta.amountIn, total + delta.total)

  val zero = BigDecimal(0)

  val gain = if (total != 0) (total - amountIn) else zero

  val gainPct = if (gain != zero) {
    100 * gain / amountIn
  } else zero

  private val spacer = " " * 61

  override def toString = f"£$amountIn%9.2f${spacer}£$total%9.2f £$gain%9.2f  $gainPct%6.2f %%"
}