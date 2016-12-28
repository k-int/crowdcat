package crowdcat

import au.com.bytecode.opencsv.CSVReader
import org.apache.commons.io.input.BOMInputStream



import grails.transaction.Transactional

@Transactional
class TSVSourceAgentService {

  def update(project, file) {
    log.debug("TSVSourceAgentService::update(${project}, ${file})");

    def bom_is = new BOMInputStream(new FileInputStream(file))
    CSVReader r = new CSVReader( new InputStreamReader(bom_is, java.nio.charset.Charset.forName('UTF-8') ), '\t' as char )
    String[] nl;
    String[] columns;
    def colmap = [:]
    def first = true

    while ((nl = r.readNext()) != null) {


      if ( first ) {
	log.debug("Header ${nl}");
        columns = nl
        first=false
      }
      else {
        def row=[:]
        int i=0;
        nl.each {
          if ( i < columns.size() ) {
            row[columns[i]]=nl[i]
          }
          i++
        }
        log.debug("Datarow ${nl} ${row}");
        if ( row.size() > 0 ) {
          processRow(project, row)
        }
      }
    }
    log.debug("TSVSourceAgentService::update done");
  }

  def private processRow(project, row) {
    log.debug("TSVSourceAgentService::processRow(${row})");
    if ( row.resource_uri && row.mainfest_url ) {
      project.ensureResourcePresent(row.resource_uri, row.mainfest_url)
    }
    log.debug("TSVSourceAgentService::processRow Done");
  }
}
