/**
 *WeatherBean.java[V 1.0.0]
 *classes : com.eric.sen5.weater.WeatherBean
 * Xlee Create at 2016-4-18 下午3:36:34
 */
package com.eric.sen5.weater;

import java.util.List;

/**
 * com.eric.sen5.weater.WeatherBean
 * 
 * @author Xlee <br/>
 *         create at 2016-4-18 下午3:36:34
 */
public class WeatherBean {
    private QueryBean query;

    public QueryBean getQuery() {
        return query;
    }

    public void setQuery(QueryBean query) {
        this.query = query;
    }

    public static class QueryBean {
        private int count;
        private String created;
        private String lang;

        private ResultsBean results;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public ResultsBean getResults() {
            return results;
        }

        public void setResults(ResultsBean results) {
            this.results = results;
        }

        public static class ResultsBean {

            private ChannelBean channel;

            public ChannelBean getChannel() {
                return channel;
            }

            public void setChannel(ChannelBean channel) {
                this.channel = channel;
            }

            public static class ChannelBean {
                /**
                 * distance : mi pressure : in speed : mph temperature : F
                 */

                private UnitsBean units;
                private String title;
                private String link;
                private String description;
                private String language;
                private String lastBuildDate;
                private String ttl;
                /**
                 * city : Shenzhen country : China region : Guangdong
                 */

                private LocationBean location;
                /**
                 * chill : 73 direction : 68 speed : 11
                 */

                private WindBean wind;
                /**
                 * humidity : 86 pressure : 1002.0 rising : 0 visibility : 16.1
                 */

                private AtmosphereBean atmosphere;
                /**
                 * sunrise : 6:0 am sunset : 6:46 pm
                 */
                private AstronomyBean astronomy;

                private ImageBean image;

                private ItemBean item;

                public UnitsBean getUnits() {
                    return units;
                }

                public void setUnits(UnitsBean units) {
                    this.units = units;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getLink() {
                    return link;
                }

                public void setLink(String link) {
                    this.link = link;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                public String getLanguage() {
                    return language;
                }

                public void setLanguage(String language) {
                    this.language = language;
                }

                public String getLastBuildDate() {
                    return lastBuildDate;
                }

                public void setLastBuildDate(String lastBuildDate) {
                    this.lastBuildDate = lastBuildDate;
                }

                public String getTtl() {
                    return ttl;
                }

                public void setTtl(String ttl) {
                    this.ttl = ttl;
                }

                public LocationBean getLocation() {
                    return location;
                }

                public void setLocation(LocationBean location) {
                    this.location = location;
                }

                public WindBean getWind() {
                    return wind;
                }

                public void setWind(WindBean wind) {
                    this.wind = wind;
                }

                public AtmosphereBean getAtmosphere() {
                    return atmosphere;
                }

                public void setAtmosphere(AtmosphereBean atmosphere) {
                    this.atmosphere = atmosphere;
                }

                public AstronomyBean getAstronomy() {
                    return astronomy;
                }

                public void setAstronomy(AstronomyBean astronomy) {
                    this.astronomy = astronomy;
                }

                public ImageBean getImage() {
                    return image;
                }

                public void setImage(ImageBean image) {
                    this.image = image;
                }

                public ItemBean getItem() {
                    return item;
                }

                public void setItem(ItemBean item) {
                    this.item = item;
                }

                public static class UnitsBean {
                    private String distance;
                    private String pressure;
                    private String speed;
                    private String temperature;

                    public String getDistance() {
                        return distance;
                    }

                    public void setDistance(String distance) {
                        this.distance = distance;
                    }

                    public String getPressure() {
                        return pressure;
                    }

                    public void setPressure(String pressure) {
                        this.pressure = pressure;
                    }

                    public String getSpeed() {
                        return speed;
                    }

                    public void setSpeed(String speed) {
                        this.speed = speed;
                    }

                    public String getTemperature() {
                        return temperature;
                    }

                    public void setTemperature(String temperature) {
                        this.temperature = temperature;
                    }
                }

                public static class LocationBean {
                    private String city;
                    private String country;
                    private String region;

                    public String getCity() {
                        return city;
                    }

                    public void setCity(String city) {
                        this.city = city;
                    }

                    public String getCountry() {
                        return country;
                    }

                    public void setCountry(String country) {
                        this.country = country;
                    }

                    public String getRegion() {
                        return region;
                    }

                    public void setRegion(String region) {
                        this.region = region;
                    }

                    @Override
                    public String toString() {
                        return "LocationBean [city=" + city + ", country=" + country + ", region=" + region + "]";
                    }
                }

                public static class WindBean {
                    private String chill;
                    private String direction;
                    private String speed;

                    public String getChill() {
                        return chill;
                    }

                    public void setChill(String chill) {
                        this.chill = chill;
                    }

                    public String getDirection() {
                        return direction;
                    }

                    public void setDirection(String direction) {
                        this.direction = direction;
                    }

                    public String getSpeed() {
                        return speed;
                    }

                    public void setSpeed(String speed) {
                        this.speed = speed;
                    }
                }

                public static class AtmosphereBean {
                    private String humidity;
                    private String pressure;
                    private String rising;
                    private String visibility;

                    public String getHumidity() {
                        return humidity;
                    }

                    public void setHumidity(String humidity) {
                        this.humidity = humidity;
                    }

                    public String getPressure() {
                        return pressure;
                    }

                    public void setPressure(String pressure) {
                        this.pressure = pressure;
                    }

                    public String getRising() {
                        return rising;
                    }

                    public void setRising(String rising) {
                        this.rising = rising;
                    }

                    public String getVisibility() {
                        return visibility;
                    }

                    public void setVisibility(String visibility) {
                        this.visibility = visibility;
                    }
                }

                public static class AstronomyBean {
                    private String sunrise;
                    private String sunset;

                    public String getSunrise() {
                        return sunrise;
                    }

                    public void setSunrise(String sunrise) {
                        this.sunrise = sunrise;
                    }

                    public String getSunset() {
                        return sunset;
                    }

                    public void setSunset(String sunset) {
                        this.sunset = sunset;
                    }
                }

                public static class ImageBean {
                    private String title;
                    private String width;
                    private String height;
                    private String link;
                    private String url;

                    public String getTitle() {
                        return title;
                    }

                    public void setTitle(String title) {
                        this.title = title;
                    }

                    public String getWidth() {
                        return width;
                    }

                    public void setWidth(String width) {
                        this.width = width;
                    }

                    public String getHeight() {
                        return height;
                    }

                    public void setHeight(String height) {
                        this.height = height;
                    }

                    public String getLink() {
                        return link;
                    }

                    public void setLink(String link) {
                        this.link = link;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }
                }

                public static class ItemBean {
                    private String title;
                    private String lat;
                    private String longX;
                    private String link;
                    private String pubDate;
                    /**
                     * code : 4 date : Mon, 18 Apr 2016 02:00 PM CST temp : 74
                     * text : Thunderstorms
                     */

                    private ConditionBean condition;
                    private String description;
                    /**
                     * isPermaLink : false
                     */

                    private GuidBean guid;
                    /**
                     * code : 4 date : 18 Apr 2016 day : Mon high : 76 low : 67
                     * text : Thunderstorms
                     */

                    private List<ForecastBean> forecast;

                    public String getTitle() {
                        return title;
                    }

                    public void setTitle(String title) {
                        this.title = title;
                    }

                    public String getLat() {
                        return lat;
                    }

                    public void setLat(String lat) {
                        this.lat = lat;
                    }

                    public String getLongX() {
                        return longX;
                    }

                    public void setLongX(String longX) {
                        this.longX = longX;
                    }

                    public String getLink() {
                        return link;
                    }

                    public void setLink(String link) {
                        this.link = link;
                    }

                    public String getPubDate() {
                        return pubDate;
                    }

                    public void setPubDate(String pubDate) {
                        this.pubDate = pubDate;
                    }

                    public ConditionBean getCondition() {
                        return condition;
                    }

                    public void setCondition(ConditionBean condition) {
                        this.condition = condition;
                    }

                    public String getDescription() {
                        return description;
                    }

                    public void setDescription(String description) {
                        this.description = description;
                    }

                    public GuidBean getGuid() {
                        return guid;
                    }

                    public void setGuid(GuidBean guid) {
                        this.guid = guid;
                    }

                    public List<ForecastBean> getForecast() {
                        return forecast;
                    }

                    public void setForecast(List<ForecastBean> forecast) {
                        this.forecast = forecast;
                    }

                    public static class ConditionBean {
                        private String code;
                        private String date;
                        private String temp;
                        private String text;

                        public String getCode() {
                            return code;
                        }

                        public void setCode(String code) {
                            this.code = code;
                        }

                        public String getDate() {
                            return date;
                        }

                        public void setDate(String date) {
                            this.date = date;
                        }

                        public String getTemp() {
                            return temp;
                        }

                        public void setTemp(String temp) {
                            this.temp = temp;
                        }

                        public String getText() {
                            return text;
                        }

                        public void setText(String text) {
                            this.text = text;
                        }
                    }

                    public static class GuidBean {
                        private String isPermaLink;

                        public String getIsPermaLink() {
                            return isPermaLink;
                        }

                        public void setIsPermaLink(String isPermaLink) {
                            this.isPermaLink = isPermaLink;
                        }
                    }

                    public static class ForecastBean {
                        private String code;
                        private String date;
                        private String day;
                        private String high;
                        private String low;
                        private String text;

                        public String getCode() {
                            return code;
                        }

                        public void setCode(String code) {
                            this.code = code;
                        }

                        public String getDate() {
                            return date;
                        }

                        public void setDate(String date) {
                            this.date = date;
                        }

                        public String getDay() {
                            return day;
                        }

                        public void setDay(String day) {
                            this.day = day;
                        }

                        public String getHigh() {
                            return high;
                        }

                        public void setHigh(String high) {
                            this.high = high;
                        }

                        public String getLow() {
                            return low;
                        }

                        public void setLow(String low) {
                            this.low = low;
                        }

                        public String getText() {
                            return text;
                        }

                        public void setText(String text) {
                            this.text = text;
                        }
                    }
                }
            }
        }
    }
}
