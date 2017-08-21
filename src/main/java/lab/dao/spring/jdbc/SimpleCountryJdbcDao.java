package lab.dao.spring.jdbc;

import java.util.List;

import lab.dao.CountryDao;
import lab.model.Country;

import lab.model.simple.SimpleCountry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class SimpleCountryJdbcDao extends NamedParameterJdbcDaoSupport implements CountryDao {
	private static final String LOAD_COUNTRIES_SQL = "insert into country (name, code_name) values ('%s', '%s');";

	private static final String GET_ALL_COUNTRIES_SQL = "select * from country";
	private static final String GET_COUNTRIES_BY_NAME_SQL = "select * from country where name like :name";
	private static final String GET_COUNTRY_BY_NAME_SQL = "select * from country where name = '";
	private static final String GET_COUNTRY_BY_CODE_NAME_SQL = "select * from country where code_name = '%s'";

	private static final String UPDATE_COUNTRY_NAME_SQL = "UPDATE country SET name = '%s' WHERE code_name = '%s'";

	private static final RowMapper<Country> COUNTRY_ROW_MAPPER = (resultSet, rowNum) ->
			new SimpleCountry(
					resultSet.getInt("id"),
					resultSet.getString("name"),
					resultSet.getString("code_name")
			);

	@Override
    public List<Country> getCountryList() {
		JdbcTemplate jdbcTemplate = getJdbcTemplate();
		if (jdbcTemplate == null) {
			throw new RuntimeException("DB is not initialized!");
		}

		return jdbcTemplate.query(GET_ALL_COUNTRIES_SQL, COUNTRY_ROW_MAPPER);
	}

	@Override
    public List<Country> getCountryListStartWith(String name) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = getNamedParameterJdbcTemplate();
		if (namedParameterJdbcTemplate == null) {
			throw new RuntimeException("DB has not initialized!");
		}

		SqlParameterSource sqlParameterSource = new MapSqlParameterSource(
				"name", name + "%");
		return namedParameterJdbcTemplate.query(GET_COUNTRIES_BY_NAME_SQL,
				sqlParameterSource, COUNTRY_ROW_MAPPER);
	}

	@Override
    public void updateCountryName(String codeName, String newCountryName) {
		JdbcTemplate jdbcTemplate = getJdbcTemplate();
		if (jdbcTemplate == null) {
			throw new RuntimeException("DB has not initialized!");
		}

		String sql = String.format(UPDATE_COUNTRY_NAME_SQL, newCountryName, codeName);
		jdbcTemplate.update(sql);
	}

	@Override
    public void loadCountries() {
		for (String[] countryData : COUNTRY_INIT_DATA) {
			String sql = String.format(LOAD_COUNTRIES_SQL, countryData[0], countryData[1]);
			getJdbcTemplate().execute(sql);
		}
	}

	@Override
    public Country getCountryByCodeName(String codeName) {
		JdbcTemplate jdbcTemplate = getJdbcTemplate();

		String sql = String.format(GET_COUNTRY_BY_CODE_NAME_SQL,  codeName);

		return jdbcTemplate.query(sql, COUNTRY_ROW_MAPPER).get(0);
	}

	@Override
    public Country getCountryByName(String name) {
		JdbcTemplate jdbcTemplate = getJdbcTemplate();
		List<Country> countryList = jdbcTemplate.query(GET_COUNTRY_BY_NAME_SQL
				+ name + "'", COUNTRY_ROW_MAPPER);
		if (countryList.isEmpty()) {
			return null;
		}
		return countryList.get(0);
	}
}
