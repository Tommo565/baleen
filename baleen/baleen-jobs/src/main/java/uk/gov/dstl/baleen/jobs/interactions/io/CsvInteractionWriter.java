package uk.gov.dstl.baleen.jobs.interactions.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.jobs.interactions.data.InteractionDefinition;

/**
 * Writes interaction data to a CSV file.
 * <p>
 * The column format is Type, Subtype, Source type, Target type, Lemma, Lemma POS, Alternatives....
 * where the 'alternatives' is a list of words which could stand in for the lemma. POS will be
 * 'noun', 'verb', 'adverb', 'adjective'.
 * <p>
 * The CSV file is RFC 4180 compliant.
 *
 */
public class CsvInteractionWriter implements InteractionWriter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CsvInteractionWriter.class);

	private final String csvFilename;

	private CSVPrinter writer;

	/**
	 * Instantiates a new csv interaction writer.
	 *
	 * @param csvFilename
	 *            the csv filename
	 */
	public CsvInteractionWriter(String csvFilename) {
		this.csvFilename = csvFilename;
	}

	@Override
	public void initialise() throws IOException {
		// Create the parent dirs
		new File(csvFilename).getAbsoluteFile().getParentFile().mkdirs();

		writer = new CSVPrinter(new FileWriter(csvFilename, false), CSVFormat.RFC4180);

		// Print the header
		writer.printRecord(new Object[] {
				"Type",
				"Subtype",
				"Source type",
				"Target type",
				"Lemma",
				"Lemma POS",
				"Alternatives...."
		});
	}

	@Override
	public void write(InteractionDefinition interaction, Collection<String> alternatives) throws IOException {
		if (writer != null) {
			final Object[] record = new Object[6 + alternatives.size()];
			record[0] = interaction.getType();
			record[1] = interaction.getSubType();
			record[2] = interaction.getSource();
			record[3] = interaction.getTarget();
			record[4] = interaction.getWord().getLemma();
			record[5] = interaction.getWord().getPos().getLabel();
			final Iterator<String> iterator = alternatives.iterator();
			for (int i = 0; i < alternatives.size(); i++) {
				record[6 + i] = iterator.next();
			}
			writer.printRecord(record);

		}
	}

	@Override
	public void destroy() {
		try {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		} catch (final Exception e) {
			LOGGER.warn("Unable to close the CSV writer", e);
			writer = null;
		}
	}

}
