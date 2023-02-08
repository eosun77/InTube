import * as React from "react";
import { styled } from "@mui/material/styles";
import ButtonBase from "@mui/material/ButtonBase";
import Typography from "@mui/material/Typography";
import one2one from "../../assets/img/one2one.jpg";
import one2n from "assets/img/one2n.jpg";
import avatar from "assets/img/avatar.jpg";

export default function AnnouncementStep1(props) {
  const images = [
    {
      url: { one2one },
      title: "1:1",
      width: "40%",
    },
    {
      url: { one2n },
      title: "1:N",
      width: "30%",
    },
    {
      url: { avatar },
      title: "avatar",
      width: "20%",
    },
  ];

  const ImageButton = styled(ButtonBase)(({ theme }) => ({
    position: "relative",
    height: 200,
    [theme.breakpoints.down("sm")]: {
      width: "100% !important", // Overrides inline-style
      height: 100,
    },
    "&:hover, &.Mui-focusVisible": {
      zIndex: 1,
      "& .MuiImageBackdrop-root": {
        opacity: 0.15,
      },
      "& .MuiImageMarked-root": {
        opacity: 0,
      },
      "& .MuiTypography-root": {
        border: "4px solid currentColor",
      },
    },
  }));

  const ImageSrc = styled("span")({
    position: "absolute",
    left: 0,
    right: 0,
    top: 0,
    bottom: 0,
    backgroundSize: "cover",
    backgroundPosition: "center 30%",
  });

  const Image = styled("span")(({ theme }) => ({
    position: "absolute",
    left: 0,
    right: 0,
    top: 0,
    bottom: 0,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: theme.palette.common.white,
  }));

  const ImageBackdrop = styled("span")(({ theme }) => ({
    position: "absolute",
    left: 0,
    right: 0,
    top: 0,
    bottom: 0,
    backgroundColor: theme.palette.common.black,
    opacity: 0.4,
    transition: theme.transitions.create("opacity"),
  }));

  const ImageMarked = styled("span")(({ theme }) => ({
    height: 3,
    width: 18,
    backgroundColor: theme.palette.common.white,
    position: "absolute",
    bottom: -2,
    left: "calc(50% - 9px)",
    transition: theme.transitions.create("opacity"),
  }));

  return (
    <div className="announcement-select" hidden={props.value !== 0}>
      {images.map((image, index) => (
        <ImageButton
          focusRipple
          key={index}
          style={{
            width: "66.7%",
            height: "230px",
            marginTop: "24px",
          }}
          onClick={(e) => {
            props.step1Handeler(e, image.title);
            props.handleChange(e, 1);
          }}
        >
          <ImageSrc style={{ backgroundImage: `url(${image.url.avatar})` }} />
          <ImageSrc style={{ backgroundImage: `url(${image.url.one2n})` }} />
          <ImageSrc style={{ backgroundImage: `url(${image.url.one2one})` }} />
          <ImageBackdrop className="MuiImageBackdrop-root" />
          <Image>
            <Typography
              component="span"
              variant="subtitle1"
              color="inherit"
              sx={{
                position: "relative",
                p: 4,
                pt: 2,
                pb: (theme) => `calc(${theme.spacing(1)} + 6px)`,
              }}
            >
              {image.title}
              <ImageMarked className="MuiImageMarked-root" />
            </Typography>
          </Image>
        </ImageButton>
      ))}
    </div>
  );
}
