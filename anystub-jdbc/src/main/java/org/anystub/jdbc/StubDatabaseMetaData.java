package org.anystub.jdbc;

import org.anystub.Decoder;
import org.anystub.Encoder;
import org.anystub.Supplier;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Arrays.asList;

// StubDatabaseMetaData is unique object in current stub
// if the field is not defined it gets real object
// each call on real object updates the records in stubfile

public class StubDatabaseMetaData implements DatabaseMetaData {
    private final StubConnection stubConnection;
    private DatabaseMetaData realDatabaseMetaData = null;
    private Optional<DatabaseMetaData> rdbmd;

    public StubDatabaseMetaData(StubConnection stubConnection) throws SQLException {

        this.stubConnection = stubConnection;
        stubConnection.add(() -> {
            realDatabaseMetaData = stubConnection.getRealConnection().getMetaData();
            rdbmd = Optional.of(realDatabaseMetaData);
        });
    }

    private String request(Supplier<String, SQLException> rs, String key) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                    @Override
                    public String get() throws SQLException {
                        stubConnection.runSql();
                        return realDatabaseMetaData == null ? null :
                                rs.get();
                    }
                }, key);
    }

    private boolean requestB(Supplier<Boolean, SQLException> rs, String... keys) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? false :
                                          rs.get();
                              }
                          },
                        keys);
    }

    private int requestI(Supplier<Integer, SQLException> rs, String... keys) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? 0 :
                                          rs.get();
                              }
                          },
                        keys);
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        return requestB(() -> realDatabaseMetaData.allProceduresAreCallable(),
                "DatabaseMetaData:allProceduresAreCallable");

    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return requestB(() -> realDatabaseMetaData.allTablesAreSelectable(),
                "DatabaseMetaData:allTablesAreSelectable");

    }

    @Override
    public String getURL() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getURL(),
                "DatabaseMetaData:getUrl");

    }

    @Override
    public String getUserName() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getUserName(),
                "DatabaseMetaData:getUserName");
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.isReadOnly(),
                "DatabaseMetaData:isReadOnly");

    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return requestB(() -> realDatabaseMetaData.nullsAreSortedHigh(),
                "DatabaseMetaData:nullsAreSortedHigh");

    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return requestB(() -> realDatabaseMetaData.nullsAreSortedLow(),
                "DatabaseMetaData:nullsAreSortedLow");

    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return requestB(() -> realDatabaseMetaData.nullsAreSortedAtStart(),
                "DatabaseMetaData:nullsAreSortedAtStart");

    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.nullsAreSortedAtEnd(),
                "DatabaseMetaData:nullsAreSortedAtEnd");

    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getDatabaseProductName(),
                "@getDatabaseProductName");
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getDatabaseProductVersion(),
                "DatabaseMetaData:getDatabaseProductVersion");
    }

    @Override
    public String getDriverName() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getDriverName(),
                "DatabaseMetaData:getDriverName");
    }

    @Override
    public String getDriverVersion() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getDriverVersion(),
                "DatabaseMetaData:getDriverVersion");
    }

    @Override
    public int getDriverMajorVersion() {
        try {
            return requestI(() -> realDatabaseMetaData.getDriverMajorVersion(),
                    "DatabaseMetaData:getDriverMajorVersion");
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public int getDriverMinorVersion() {
        try {
            return requestI(() -> realDatabaseMetaData.getDriverMinorVersion(),
                    "DatabaseMetaData:getDriverMinorVersion");
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.usesLocalFiles(),
                "DatabaseMetaData:usesLocalFiles");
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.usesLocalFilePerTable(),
                "DatabaseMetaData:usesLocalFilePerTable");
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsMixedCaseIdentifiers(),
                "DatabaseMetaData:supportsMixedCaseIdentifiers");
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.storesUpperCaseIdentifiers(),
                "DatabaseMetaData:storesUpperCaseIdentifiers");
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.storesLowerCaseIdentifiers(),
                "DatabaseMetaData:storesLowerCaseIdentifiers");
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.storesMixedCaseIdentifiers(),
                "DatabaseMetaData:storesMixedCaseIdentifiers");
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsMixedCaseQuotedIdentifiers(),
                "DatabaseMetaData:supportsMixedCaseQuotedIdentifiers");
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.storesUpperCaseQuotedIdentifiers(),
                "DatabaseMetaData:storesUpperCaseQuotedIdentifiers");
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.storesLowerCaseQuotedIdentifiers(),
                "DatabaseMetaData:storesLowerCaseQuotedIdentifiers");
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.storesMixedCaseQuotedIdentifiers(),
                "DatabaseMetaData:storesMixedCaseQuotedIdentifiers");
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getIdentifierQuoteString(),
                "DatabaseMetaData:getIdentifierQuoteString");
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getSQLKeywords(),
                "DatabaseMetaData:getSQLKeywords");
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return request(() -> realDatabaseMetaData.getNumericFunctions(),
                "DatabaseMetaData:getNumericFunctions");
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getStringFunctions(),
                "DatabaseMetaData:getStringFunctions");
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getSystemFunctions(),
                "DatabaseMetaData:getSystemFunctions");
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getTimeDateFunctions(),
                "DatabaseMetaData:getTimeDateFunctions");
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getSearchStringEscape(),
                "DatabaseMetaData:getSearchStringEscape");
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getExtraNameCharacters(),
                "DatabaseMetaData:getExtraNameCharacters");
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsAlterTableWithAddColumn()
                ,
                "DatabaseMetaData:supportsAlterTableWithAddColumn");
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsAlterTableWithDropColumn()
                ,
                "DatabaseMetaData:supportsAlterTableWithDropColumn");
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsColumnAliasing()
                ,
                "DatabaseMetaData:supportsColumnAliasing");
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.nullPlusNonNullIsNull()
                ,
                "DatabaseMetaData:nullPlusNonNullIsNull");
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsConvert()
                ,
                "DatabaseMetaData:supportsConvert");
    }

    @Override
    public boolean supportsConvert(int i, int i1) throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsConvert(i, i1),
                "DatabaseMetaData:supportsConvert", String.valueOf(i), String.valueOf(i1));
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsTableCorrelationNames()
                ,
                "DatabaseMetaData:supportsTableCorrelationNames");
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsDifferentTableCorrelationNames()
                ,
                "DatabaseMetaData:supportsDifferentTableCorrelationNames");
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsExpressionsInOrderBy()
                ,
                "DatabaseMetaData:supportsExpressionsInOrderBy");
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsOrderByUnrelated()
                ,
                "DatabaseMetaData:supportsOrderByUnrelated");
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsGroupBy()
                ,
                "DatabaseMetaData:supportsGroupBy");
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsGroupByUnrelated()
                ,
                "DatabaseMetaData:supportsGroupByUnrelated");
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsGroupByBeyondSelect(),
                "DatabaseMetaData:supportsGroupByBeyondSelect");
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsLikeEscapeClause()
                ,
                "DatabaseMetaData:supportsLikeEscapeClause");
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsMultipleResultSets()
                ,
                "DatabaseMetaData:supportsMultipleResultSets");
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsMultipleTransactions()
                ,
                "DatabaseMetaData:supportsMultipleTransactions");
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsNonNullableColumns()
                ,
                "DatabaseMetaData:supportsNonNullableColumns");
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsMinimumSQLGrammar()
                ,
                "DatabaseMetaData:supportsMinimumSQLGrammar");
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsCoreSQLGrammar()
                ,
                "DatabaseMetaData:supportsCoreSQLGrammar");
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsExtendedSQLGrammar()
                ,
                "DatabaseMetaData:supportsExtendedSQLGrammar");
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsANSI92EntryLevelSQL()
                ,
                "DatabaseMetaData:supportsANSI92EntryLevelSQL");
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsANSI92IntermediateSQL()
                ,
                "DatabaseMetaData:supportsANSI92IntermediateSQL");
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsANSI92FullSQL()
                ,
                "DatabaseMetaData:supportsANSI92FullSQL");
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsIntegrityEnhancementFacility()
                ,
                "DatabaseMetaData:supportsIntegrityEnhancementFacility");
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsOuterJoins()
                ,
                "DatabaseMetaData:supportsOuterJoins");
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsFullOuterJoins()
                ,
                "DatabaseMetaData:supportsFullOuterJoins");
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsLimitedOuterJoins()
                ,
                "DatabaseMetaData:supportsLimitedOuterJoins");
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getSchemaTerm(),
                "DatabaseMetaData:getSchemaTerm");
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getProcedureTerm(),
                "DatabaseMetaData:getProcedureTerm");
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getCatalogTerm(),
                "DatabaseMetaData:getCatalogTerm");
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return requestB(() -> realDatabaseMetaData.isCatalogAtStart()
                ,
                "DatabaseMetaData:isCatalogAtStart");
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        return request(() ->
                        realDatabaseMetaData.getCatalogSeparator(),
                "DatabaseMetaData:getCatalogSeparator");
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsSchemasInDataManipulation()
                ,
                "DatabaseMetaData:supportsSchemasInDataManipulation");
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsSchemasInProcedureCalls()
                ,
                "DatabaseMetaData:supportsSchemasInProcedureCalls");
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsSchemasInTableDefinitions()
                ,
                "DatabaseMetaData:supportsSchemasInTableDefinitions");
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsSchemasInIndexDefinitions()
                ,
                "DatabaseMetaData:supportsSchemasInIndexDefinitions");
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsSchemasInPrivilegeDefinitions()
                ,
                "DatabaseMetaData:supportsSchemasInPrivilegeDefinitions");
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsCatalogsInDataManipulation(),
                "DatabaseMetaData:supportsCatalogsInDataManipulation");
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsCatalogsInProcedureCalls(),
                "DatabaseMetaData:supportsCatalogsInProcedureCalls");
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsCatalogsInTableDefinitions(),
                "DatabaseMetaData:supportsCatalogsInTableDefinitions");
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsCatalogsInIndexDefinitions(),
                "DatabaseMetaData:supportsCatalogsInIndexDefinitions");
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsCatalogsInPrivilegeDefinitions(),
                "DatabaseMetaData:supportsCatalogsInPrivilegeDefinitions");
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsPositionedDelete(),
                "DatabaseMetaData:supportsPositionedDelete");
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsPositionedUpdate()
                ,
                "DatabaseMetaData:supportsPositionedUpdate");
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsSelectForUpdate()
                ,
                "DatabaseMetaData:supportsSelectForUpdate");
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsStoredProcedures()
                ,
                "DatabaseMetaData:supportsStoredProcedures");
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsSubqueriesInComparisons()
                ,
                "DatabaseMetaData:supportsSubqueriesInComparisons");
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsSubqueriesInExists()
                ,
                "DatabaseMetaData:supportsSubqueriesInExists");
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsSubqueriesInIns()
                ,
                "DatabaseMetaData:supportsSubqueriesInIns");
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsSubqueriesInQuantifieds()
                ,
                "DatabaseMetaData:supportsSubqueriesInQuantifieds");
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsCorrelatedSubqueries()
                ,
                "DatabaseMetaData:supportsCorrelatedSubqueries");
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsUnion()
                ,
                "DatabaseMetaData:supportsUnion");
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsUnionAll()
                ,
                "DatabaseMetaData:supportsUnionAll");
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsOpenCursorsAcrossCommit()
                ,
                "DatabaseMetaData:supportsOpenCursorsAcrossCommit");
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsOpenCursorsAcrossRollback()
                ,
                "DatabaseMetaData:supportsOpenCursorsAcrossRollback");
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsOpenStatementsAcrossCommit()
                ,
                "DatabaseMetaData:supportsOpenStatementsAcrossCommit");
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsOpenStatementsAcrossRollback()
                ,
                "DatabaseMetaData:supportsOpenStatementsAcrossRollback");
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxBinaryLiteralLength(),
                "DatabaseMetaData:getMaxBinaryLiteralLength");
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxCharLiteralLength()

                ,
                "DatabaseMetaData:getMaxCharLiteralLength");
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxColumnNameLength(),
                "DatabaseMetaData:getMaxColumnNameLength");
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxColumnsInGroupBy(),
                "DatabaseMetaData:getMaxColumnsInGroupBy");
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxColumnsInIndex(),
                "DatabaseMetaData:getMaxColumnsInIndex");
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxColumnsInOrderBy(),
                "DatabaseMetaData:getMaxColumnsInOrderBy");
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxColumnsInSelect()
                ,
                "DatabaseMetaData:getMaxColumnsInSelect");
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxColumnsInTable()
                ,
                "DatabaseMetaData:getMaxColumnsInTable");
    }

    @Override
    public int getMaxConnections() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxConnections()
                ,
                "DatabaseMetaData:getMaxConnections");
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxCursorNameLength()
                ,
                "DatabaseMetaData:getMaxCursorNameLength");
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxIndexLength()
                ,
                "DatabaseMetaData:getMaxIndexLength");
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxSchemaNameLength()
                ,
                "DatabaseMetaData:getMaxSchemaNameLength");
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxProcedureNameLength()
                ,
                "DatabaseMetaData:getMaxProcedureNameLength");
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxCatalogNameLength()
                ,
                "DatabaseMetaData:getMaxCatalogNameLength");
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxRowSize()
                ,
                "DatabaseMetaData:getMaxRowSize");
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.doesMaxRowSizeIncludeBlobs()
                ,
                "DatabaseMetaData:doesMaxRowSizeIncludeBlobs");
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxStatementLength()
                ,
                "DatabaseMetaData:getMaxStatementLength");
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxStatements()
                ,
                "DatabaseMetaData:getMaxStatements");
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxTableNameLength()
                ,
                "DatabaseMetaData:getMaxTableNameLength");
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxTablesInSelect()
                ,
                "DatabaseMetaData:getMaxTablesInSelect");
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getMaxUserNameLength()
                ,
                "DatabaseMetaData:getMaxUserNameLength");
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getDefaultTransactionIsolation(),
                "DatabaseMetaData:getDefaultTransactionIsolation");
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsTransactions()
                ,
                "DatabaseMetaData:supportsTransactions");
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int i) throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsTransactionIsolationLevel(i),
                "DatabaseMetaData:supportsTransactionIsolationLevel", String.valueOf(i));
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsDataDefinitionAndDataManipulationTransactions(),
                "DatabaseMetaData:supportsDataDefinitionAndDataManipulationTransactions");
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsDataManipulationTransactionsOnly(),
                "DatabaseMetaData:supportsDataManipulationTransactionsOnly");
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.dataDefinitionCausesTransactionCommit(),
                "DatabaseMetaData:dataDefinitionCausesTransactionCommit");
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.dataDefinitionIgnoredInTransactions()
                ,
                "DatabaseMetaData:dataDefinitionIgnoredInTransactions");
    }

    @Override
    public ResultSet getProcedures(String s, String s1, String s2) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getProcedures(s, s1, s2);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        "DatabaseMetaData:getProcedures", s, s1, s2);
    }

    @Override
    public ResultSet getProcedureColumns(String s, String s1, String s2, String s3) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getProcedureColumns(s, s1, s2, s3);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        "DatabaseMetaData:getProcedureColumns", s, s1, s2, s3);
    }

    @Override
    public ResultSet getTables(String s, String s1, String s2, String[] strings) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getTables(s, s1, s2, strings);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        "DatabaseMetaData:getTables", s, s1, s2, Arrays.toString(strings));
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getSchemas();
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        "DatabaseMetaData:getSchemas");
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getCatalogs();
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        "DatabaseMetaData:getCatalogs");
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getTableTypes();
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        "DatabaseMetaData:getTableTypes");
    }

    @Override
    public ResultSet getColumns(String s, String s1, String s2, String s3) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getColumns(s, s1, s2, s3);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getColumns", s, s1, s2, s3);
    }

    @Override
    public ResultSet getColumnPrivileges(String s, String s1, String s2, String s3) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getColumnPrivileges(s, s1, s2, s3);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getColumnPrivileges", s, s1, s2, s3);
    }

    @Override
    public ResultSet getTablePrivileges(String s, String s1, String s2) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getTablePrivileges(s, s1, s2);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getTablePrivileges", s, s1, s2);
    }

    @Override
    public ResultSet getBestRowIdentifier(String s, String s1, String s2, int i, boolean b) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getBestRowIdentifier(s, s1, s2, i, b);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getBestRowIdentifier", s, s1, s2, String.valueOf(i), String.valueOf(b));
    }

    @Override
    public ResultSet getVersionColumns(String s, String s1, String s2) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getVersionColumns(s, s1, s2);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getVersionColumns", s, s1, s2);
    }

    @Override
    public ResultSet getPrimaryKeys(String s, String s1, String s2) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getPrimaryKeys(s, s1, s2);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getPrimaryKeys", s, s1, s2);
    }

    @Override
    public ResultSet getImportedKeys(String s, String s1, String s2) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getImportedKeys(s, s1, s2);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getImportedKeys", s, s1, s2);
    }

    @Override
    public ResultSet getExportedKeys(String s, String s1, String s2) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getExportedKeys(s, s1, s2);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getExportedKeys", s, s1, s2);
    }

    @Override
    public ResultSet getCrossReference(String s, String s1, String s2, String s3, String s4, String s5) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getCrossReference(s, s1, s2, s3, s4, s5);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getCrossReference", s, s1, s2, s3, s4, s5);
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getTypeInfo();
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        "DatabaseMetaData:getTypeInfo");
    }

    @Override
    public ResultSet getIndexInfo(String s, String s1, String s2, boolean b, boolean b1) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getIndexInfo(s, s1, s2, b, b1);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        "DatabaseMetaData:getIndexInfo", s, s1, s2, String.valueOf(b), String.valueOf(b1));
    }

    @Override
    public boolean supportsResultSetType(int i) throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsResultSetType(i)
                ,
                "DatabaseMetaData:supportsResultSetType", String.valueOf(i));
    }

    @Override
    public boolean supportsResultSetConcurrency(int i, int i1) throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsResultSetConcurrency(i, i1),
                "DatabaseMetaData:", String.valueOf(i), String.valueOf(i1));
    }

    @Override
    public boolean ownUpdatesAreVisible(int i) throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.ownUpdatesAreVisible(i),
                "DatabaseMetaData:ownUpdatesAreVisible", String.valueOf(i));
    }

    @Override
    public boolean ownDeletesAreVisible(int i) throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.ownDeletesAreVisible(i),
                "DatabaseMetaData:ownDeletesAreVisible", String.valueOf(i));
    }

    @Override
    public boolean ownInsertsAreVisible(int i) throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.ownInsertsAreVisible(i),
                "DatabaseMetaData:ownInsertsAreVisible", String.valueOf(i));
    }

    @Override
    public boolean othersUpdatesAreVisible(int i) throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.othersUpdatesAreVisible(i),
                "DatabaseMetaData:othersUpdatesAreVisible");
    }

    @Override
    public boolean othersDeletesAreVisible(int i) throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.othersDeletesAreVisible(i),
                "DatabaseMetaData:othersDeletesAreVisible", String.valueOf(i));
    }

    @Override
    public boolean othersInsertsAreVisible(int i) throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.othersInsertsAreVisible(i),
                "DatabaseMetaData:othersInsertsAreVisible", String.valueOf(i));
    }

    @Override
    public boolean updatesAreDetected(int i) throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.updatesAreDetected(i),
                "DatabaseMetaData:updatesAreDetected", String.valueOf(i));
    }

    @Override
    public boolean deletesAreDetected(int i) throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.deletesAreDetected(i),
                "DatabaseMetaData:deletesAreDetected", String.valueOf(i));
    }

    @Override
    public boolean insertsAreDetected(int i) throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.insertsAreDetected(i),
                "DatabaseMetaData:insertsAreDetected", String.valueOf(i));
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsBatchUpdates(),
                "DatabaseMetaData:supportsBatchUpdates");
    }

    @Override
    public ResultSet getUDTs(String s, String s1, String s2, int[] ints) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getUDTs(s, s1, s2, ints);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        "DatabaseMetaData:getUDTs", s, s1, s2, Arrays.toString(ints));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return stubConnection;
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsSavepoints(),
                "DatabaseMetaData:supportsSavepoints");
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsNamedParameters(),
                "DatabaseMetaData:supportsNamedParameters");
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsMultipleOpenResults(),
                "DatabaseMetaData:supportsMultipleOpenResults");
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsGetGeneratedKeys(),
                "DatabaseMetaData:supportsGetGeneratedKeys");
    }

    @Override
    public ResultSet getSuperTypes(String s, String s1, String s2) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getSuperTypes(s, s1, s2);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getSuperTypes", s, s1, s2);

    }

    @Override
    public ResultSet getSuperTables(String s, String s1, String s2) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getSuperTables(s, s1, s2);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        "DatabaseMetaData:getSuperTables", s, s1, s2);
    }

    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:", catalog, schemaPattern, typeNamePattern, attributeNamePattern);
    }

    @Override
    public boolean supportsResultSetHoldability(int i) throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsResultSetHoldability(i),
                "DatabaseMetaData:supportsResultSetHoldability", String.valueOf(i));
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getResultSetHoldability()
                ,
                "DatabaseMetaData:getResultSetHoldability");
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getDatabaseMajorVersion()
                ,
                "DatabaseMetaData:getDatabaseMajorVersion");
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getDatabaseMinorVersion()
                ,
                "DatabaseMetaData:getDatabaseMinorVersion");
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getJDBCMajorVersion()
                ,
                "DatabaseMetaData:getJDBCMajorVersion");
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return requestI(() ->
                        realDatabaseMetaData.getJDBCMinorVersion()
                ,
                "DatabaseMetaData:getJDBCMinorVersion");
    }

    @Override
    public int getSQLStateType() throws SQLException {
        return requestI(() -> realDatabaseMetaData.getSQLStateType(),
                "DatabaseMetaData:getSQLStateType");
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        return requestB(() -> realDatabaseMetaData.locatorsUpdateCopy(),
                "DatabaseMetaData:locatorsUpdateCopy");
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return requestB(() -> realDatabaseMetaData.supportsStatementPooling(),
                "DatabaseMetaData:supportsStatementPooling");
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<RowIdLifetime, SQLException>() {
                              @Override
                              public RowIdLifetime get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getRowIdLifetime();
                              }
                          },
                        new Decoder<RowIdLifetime>() {
                            @Override
                            public RowIdLifetime decode(Iterable<String> values) {
                                if (values == null) {
                                    return RowIdLifetime.ROWID_UNSUPPORTED;
                                }
                                return RowIdLifetime.valueOf(values.iterator().next());
                            }
                        }, new Encoder<RowIdLifetime>() {
                            @Override
                            public Iterable<String> encode(RowIdLifetime resultSet) {
                                return asList(resultSet.name());
                            }
                        },
                        "DatabaseMetaData:RowIdLifetime");
    }

    @Override
    public ResultSet getSchemas(String s, String s1) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getSchemas(s, s1);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getSchemas", s, s1);
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.supportsStoredFunctionsUsingCallSyntax(),
                "DatabaseMetaData:supportsStoredFunctionsUsingCallSyntax");
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return requestB(() ->
                        realDatabaseMetaData.autoCommitFailureClosesAllResultSets(),
                "DatabaseMetaData:autoCommitFailureClosesAllResultSets");
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getClientInfoProperties();
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getClientInfoProperties");
    }

    @Override
    public ResultSet getFunctions(String s, String s1, String s2) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getFunctions(s, s1, s2);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getFunctions", s, s1, s2);
    }

    @Override
    public ResultSet getFunctionColumns(String s, String s1, String s2, String s3) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getFunctionColumns(s, s1, s2, s3);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getFunctionColumns", s, s1, s2, s3);
    }

    @Override
    public ResultSet getPseudoColumns(String s, String s1, String s2, String s3) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return realDatabaseMetaData == null ? null
                                          : realDatabaseMetaData.getPseudoColumns(s, s1, s2, s3);
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),

                        "DatabaseMetaData:getPseudoColumns", s1, s2, s3);
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return requestB(() -> realDatabaseMetaData.generatedKeyAlwaysReturned(),
                "DatabaseMetaData:generatedKeyAlwaysReturned");
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return requestB(() -> realDatabaseMetaData.isWrapperFor(aClass),
                "DatabaseMetaData:isWrapperFor", aClass.getCanonicalName());
    }
}
