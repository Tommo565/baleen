package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class NormalizeTemporalTest extends AbstractAnnotatorTest {

	public NormalizeTemporalTest() {
		super(NormalizeTemporal.class);
	}

	@Test
	public void testTime() throws Exception{
		jCas.setDocumentText("It was midnight, and all was quiet");
		
		Temporal t = new Temporal(jCas, 7, 15);
		t.setTimestampStart(LocalDateTime.of(2016, 10, 4, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
		t.setTimestampStop(LocalDateTime.of(2016, 10, 4, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
		t.setScope("SINGLE");
		t.setTemporalType("TIME");
		t.setValue("midnight");
		t.addToIndexes();
		
		processJCas(NormalizeTemporal.PARAM_DATE_FORMAT, "HH:mm", NormalizeTemporal.PARAM_TEMPORAL_TYPE, "TIME");
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		Temporal tTest = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("00:00", tTest.getValue());
	}
	
	@Test
	public void testWrongType() throws Exception{
		jCas.setDocumentText("It was midnight, and all was quiet");
		
		Temporal t = new Temporal(jCas, 7, 15);
		t.setTimestampStart(LocalDateTime.of(2016, 10, 4, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
		t.setTimestampStop(LocalDateTime.of(2016, 10, 4, 0, 0, 0).toEpochSecond(ZoneOffset.UTC));
		t.setScope("SINGLE");
		t.setTemporalType("TIME");
		t.setValue("midnight");
		t.addToIndexes();
		
		processJCas(NormalizeTemporal.PARAM_DATE_FORMAT, "HH:mm", NormalizeTemporal.PARAM_TEMPORAL_TYPE, "DATE");
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		Temporal tTest = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("midnight", tTest.getValue());
	}
}
