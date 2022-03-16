import React, { useState } from 'react';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import ThumbUpAltIcon from '@material-ui/icons/ThumbUpAlt';
import ThumbDownAltIcon from '@material-ui/icons/ThumbDownAlt';
import { makeStyles, withStyles } from '@material-ui/core/styles';
import Rating from '@material-ui/lab/Rating';
import Popover from '@material-ui/core/Popover';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import Box from '@material-ui/core/Box';
import TextareaAutosize from '@material-ui/core/TextareaAutosize';
import TextField from '@material-ui/core/TextField';
import FeedbackStyle from "../../styles/Feedback";
import { translate } from "../../../assets/localisation";
import '../../styles/css/GlobalCssSlider.css';


function SimpleDialogDemo(props) {
  const { classes } = props;
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [anchorE2, setAnchorE2] = React.useState(null);
  const [value, setValue] = React.useState(0);

  const iconStyle = {
    width: 100,
    "&:hover": {

      backgroundColor: "#FFFF"
    }

  }
  const smallDistanceStyle = {
    width: 100,
    height: 100,
    padding: 50
  }


  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };
  const handleClosefeedback = () => {
    setAnchorE2(null);
  }
  const handleClickfeedback = (event) => {
    handleClose();
    setAnchorE2(event.currentTarget);
  };

  const open1 = Boolean(anchorE2);
  const id1 = open1 ? 'simple-popover' : undefined;


  const open = Boolean(anchorEl);
  const id = open ? 'simple-popover' : undefined;
  console.log(anchorEl)

  const divStyle = {
    display: 'flex',
    alignItems: 'center'
  };
  return (

    <div >
      <Button variant="contained" size="small" className={classes.feedbackbutton} onClick={handleClick}>
        <ThumbUpAltIcon className={classes.feedbackIcon} />
        < ThumbDownAltIcon className={classes.feedbackIcon} />
        <Typography variant="body2" className={classes.feedbackTitle} > {translate("button:feedback")}</Typography>
      </Button>
      <Popover
        id={id}
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: '',
          horizontal: 'right',
        }}
        transformOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
      >




        <Typography className={classes.typography} align="center" >   {translate("lable.feedback1")} <br />  {translate("lable.feedbacks")}</Typography>


        <Rating
          itemStyle={smallDistanceStyle}
          itemIconStyle={iconStyle}
          className={classes.rating}
          size="large"
          name="simple-controlled"
          value={value}
          onChange={(event, newValue) => {
            setValue(newValue);
          }}

        />
        < Typography className={classes.feedbacktypography} variant="body2"  >  {translate("lable.verybad")}  < Typography variant="body2" style={{ float: "right", fontSize: "12px" }} >  {translate("lable.verygood")}  </Typography>   </Typography>

        <div className={classes.root}>

          <Grid container justifyContent="center">
            <Grid item>

              <Link
                component="button"
                variant="body2"
                onClick={handleClickfeedback}
                style={{ color: "#FD7F23", fontSize: "13px", textDecoration: "underline" }}
              >
                {translate("link.feedback")}
              </Link>

            </Grid>
          </Grid>

        </div>

        <Button variant="outlined" size="small" color="primary" className={classes.submitbutton}  >
          {translate("button.submit")}
        </Button>

        <Typography className={classes.typographys} align="center" variant="body2" component="div" >
          {translate("lable.feedback2")}</Typography>


      </Popover>

      <Popover

        id={id1}
        open={open1}
        anchorE2={anchorE2}
        onClose={handleClosefeedback}
        // anchorOrigin={{
        //   vertical: 'center',
        //   horizontal: 'right',
        // }}
        // transformOrigin={{
        //   vertical: '',
        //   horizontal: 'left',
        // }}
        PaperProps={{
          style: { width: '21%' },
        }}
        anchorReference="anchorPosition"
        anchorPosition={{ top: 214, left: 1148, }}


      >
        <Typography variant="body2" className={classes.typography2}> {translate("lable.feedback3")}</Typography>
        <Box p={5}>

          <Typography variant="body2" className={classes.typography1}>Rate  <span style={{ fontWeight: "bold" }}>Speech to Text</span> Quality</Typography>
          <Rating name="size-medium" />
          <Button className={classes.buttonsuggest} variant="outlined" size="small" color="primary" >
            <Typography variant="body2" color="primary" > {translate("button.Suggest an edit")}</Typography>

          </Button>
          <Typography variant="body2" className={classes.typography1}>Rate <span style={{ fontWeight: "bold" }}  >Translate  Text</span>  Quality</Typography>
          <Rating name="size-medium" />
          <Button variant="outlined" size="small" color="primary" className={classes.buttonsuggest}>
            <Typography variant="body2" color="primary">  {translate("button.Suggest an edit")}</Typography>

          </Button>
          <Typography variant="body2" className={classes.typography1} >Rate  <span style={{ fontWeight: "bold" }}>Translated Speech</span> Quality </Typography>
          <Rating name="size-medium" />
        </Box>
        <div style={{ borderBottom: "1px solid #ECE7E6 ", width: "240px", margin: "auto", paddingBottom: "20px" }}></div>

        <Typography variant="body2" style={{ margin: "10px 10px 10px 10px" }}> {translate("lable.feedback4")}</Typography>
        <Grid container justifyContent="center">
          <Grid item>
            <TextareaAutosize
              aria-label="minimum height"
              minRows={4}
              className={classes.textareaAutosize}
              style={{ width: 250 }}
            />

          </Grid>
          <Grid container justifyContent="center">
            <Grid items>
              <Button variant="outlined" size="small" color="primary" style={{ margin: "10px" }}  >
                {translate("button.submit")}
              </Button>
            </Grid>
          </Grid>
        </Grid>
      </Popover>

    </div>
  );
}
export default withStyles(FeedbackStyle)(SimpleDialogDemo);
