import React from "react";
import axios from "axios";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Divider from "@mui/material/Divider";
import PersonRoundedIcon from "@mui/icons-material/PersonRounded";
import ContentPasteRoundedIcon from "@mui/icons-material/ContentPasteRounded";
import AccessTimeRoundedIcon from "@mui/icons-material/AccessTimeRounded";
import PaidRoundedIcon from "@mui/icons-material/PaidRounded";
import InterviewListItemDetail from "components/Interview/InterviewListItemDetail";
import InterviewListItemTag from "components/Interview/InterviewListItemTag";
import "components/Interview/InterviewListItem.css";

export default function InterviewListItem(props) {
  const [open, setOpen] = React.useState(false);
  const handleClickOpen = () => () => {
    setOpen(true);
  };

  return (
    <div>
      <Card sx={{ minHeight: 340 }} onClick={handleClickOpen()}>
        <CardContent>
          <InterviewListItemTag
            interview={props.interview}
          ></InterviewListItemTag>
          <Typography
            align="left"
            gutterBottom
            variant="h5"
            component="div"
            className="title"
          >
            <div className="interview-list-item-title">
              {props.interview.title}
            </div>
          </Typography>

          <Divider />

          <List>
            <ListItem disablePadding>
              <ListItemIcon>
                <ContentPasteRoundedIcon />
              </ListItemIcon>
              <ListItemText
                primary={props.interview.description}
                className="interview-list-item-description"
              />
            </ListItem>
            <ListItem disablePadding>
              <ListItemIcon>
                <PersonRoundedIcon />
              </ListItemIcon>
              <ListItemText>
                {props.interview.start_standard_age}~
                {props.interview.end_standard_age}세{" "}
                {props.interview.gender === "W" && "여성"}
                {props.interview.gender === "M" && "남성"}
                {props.interview.gender === "O" && "상관없음"}
              </ListItemText>
            </ListItem>
            <ListItem disablePadding>
              <ListItemIcon>
                <AccessTimeRoundedIcon />
              </ListItemIcon>
              <ListItemText>
                {props.interview.apply_start_time} ~
                {props.interview.apply_end_time}
              </ListItemText>
            </ListItem>
            <ListItem disablePadding>
              <ListItemIcon>
                <PaidRoundedIcon />
              </ListItemIcon>
              <ListItemText primary={props.interview.standard_point} />
            </ListItem>
          </List>
        </CardContent>
      </Card>

      <InterviewListItemDetail
        open={open}
        setOpen={setOpen}
        interview={props.interview}
      />
    </div>
  );
}
