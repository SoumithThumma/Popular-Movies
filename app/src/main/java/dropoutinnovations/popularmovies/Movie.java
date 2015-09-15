package dropoutinnovations.popularmovies;

public class Movie {
    String Poster, Title, Date, Rating, Plot, DetailPoster;
    int ID, Favorite;

    public Movie(String poster, String title, String rating, String date, String plot, String detailPoster, int id, int favorite) {
        Poster = "http://image.tmdb.org/t/p/w500/" + poster;
        Title = title;
        Date = date;
        Rating = rating;
        Plot = plot;
        DetailPoster = "http://image.tmdb.org/t/p/w780/" + detailPoster;
        ID = id;
        Favorite = favorite;
    }

    public int getFavorite() {
        return Favorite;
    }
    public void setFavorite(int num) {
        Favorite = num;
    }

    public int getID() {
        return ID;
    };

    public String getPoster() {
        return Poster;
    }

    public String getPlot() {
        return Plot;
    }

    public String getDetailPoster() {
        return DetailPoster;
    }

    public String getTitle() {
        return Title;
    }

    public String getDate() {
        return Date;
    }

    public String getRating() {
        return Rating + "/10";
    }
}